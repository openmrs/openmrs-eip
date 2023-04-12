package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_BACKLOG_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_COUNT_CACHE_TTL;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_DISABLED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_PRIORITIZE_THRESHOLD;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TIME_PER_ITEM;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Base class for {@link Runnable} instances that process tasks for a single site
 */
public abstract class BaseSiteRunnable implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BaseSiteRunnable.class);
	
	//Sync takes on average 3-5 seconds based on prod stats at the time of writing this
	protected static final int DEFAULT_BACK_LOG = 2;
	
	protected static final int DEFAULT_SYNC_TIME_PER_ITEM = 4000;
	
	//1hr estimate is based on the time it takes to process about 1000 messages per thread
	protected static final long DEFAULT_SIZE_REFRESH_INTERVAL = 3600000;
	
	protected static final String KEY_SYNC_COUNT = "sync_count";
	
	private static boolean initialized = false;
	
	private static boolean syncPrioritizeDisabled;
	
	private static int syncThreshold;
	
	private static long countCacheTtl;
	
	private static Map<String, Long> countMap = null;
	
	protected static Pageable page;
	
	private boolean errorEncountered = false;
	
	protected SiteInfo site;
	
	protected SyncMessageRepository syncRepo;
	
	public BaseSiteRunnable(SiteInfo site) {
		this.site = site;
		syncRepo = SyncContext.getBean(SyncMessageRepository.class);
		initIfNecessary();
	}
	
	protected void initIfNecessary() {
		synchronized (BaseSiteRunnable.class) {
			if (!initialized) {
				Environment e = SyncContext.getBean(Environment.class);
				page = PageRequest.of(0, e.getProperty(PROP_SYNC_TASK_BATCH_SIZE, Integer.class, DEFAULT_TASK_BATCH_SIZE));
				syncPrioritizeDisabled = e.getProperty(PROP_PRIORITIZE_DISABLED, Boolean.class, false);
				if (!syncPrioritizeDisabled) {
					log.info("Initializing sync prioritization configuration");
					
					int backlogDays = e.getProperty(PROP_BACKLOG_THRESHOLD, Integer.class, DEFAULT_BACK_LOG);
					//TODO Replace this property by timing the sync process and use that
					int timePerItem = e.getProperty(PROP_SYNC_TIME_PER_ITEM, int.class, DEFAULT_SYNC_TIME_PER_ITEM);
					int syncThresholdPerThread = (backlogDays * 86400000) / timePerItem;
					log.info("Projected default sync threshold per thread: " + syncThresholdPerThread);
					
					//If the sync count is more than our available CPU cores can process in a determined period by 
					//default we want message sync prioritization to kick in unless the user defined their own
					ThreadPoolExecutor syncExecutor = SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR);
					int defaultSyncThreshold = syncThresholdPerThread * syncExecutor.getMaximumPoolSize();
					log.info("Projected default sync threshold: " + defaultSyncThreshold);
					
					syncThreshold = e.getProperty(PROP_PRIORITIZE_THRESHOLD, Integer.class, defaultSyncThreshold);
					countCacheTtl = e.getProperty(PROP_COUNT_CACHE_TTL, Long.class, DEFAULT_SIZE_REFRESH_INTERVAL);
					
					log.info("Sync prioritization configuration -> queue threshold: " + syncThreshold + ", count cache TTL: "
					        + Duration.of(countCacheTtl, ChronoUnit.MILLIS).toMinutes() + " minutes");
					
					//TODO Replace with a simpler variable holder class that periodically expires the variable values
					countMap = Collections.synchronizedMap(new PassiveExpiringMap(countCacheTtl, new HashMap(1)));
					countMap.put(KEY_SYNC_COUNT, getSyncCount());
				}
				
				initialized = true;
			}
		}
	}
	
	@Override
	public void run() {
		final String originalThreadName = Thread.currentThread().getName();
		
		try {
			Thread.currentThread().setName(Thread.currentThread().getName() + ":" + site.getName() + ":" + getTaskName());
			if (AppUtils.isStopping()) {
				if (log.isDebugEnabled()) {
					log.debug("Skipping run because the application is stopping");
				}
				
				return;
			}
			
			if (!syncPrioritizeDisabled && isSyncSizeThresholdExceeded()) {
				return;
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Starting");
			}
			
			do {
				try {
					boolean stop = doRun();
					if (stop) {
						break;
					}
				}
				catch (Throwable t) {
					if (!AppUtils.isAppContextStopping()) {
						errorEncountered = true;
						String msg = "An error has been encountered";
						if (log.isDebugEnabled()) {
							log.error(msg, t);
						} else {
							log.warn(msg);
						}
						
						break;
					}
				}
			} while (!AppUtils.isStopping() && !errorEncountered);
			
			if (!errorEncountered) {
				if (log.isDebugEnabled()) {
					log.debug("Completed");
				}
			}
		}
		finally {
			Thread.currentThread().setName(originalThreadName);
		}
	}
	
	protected boolean isSyncSizeThresholdExceeded() {
		Long c = countMap.get(KEY_SYNC_COUNT);
		if (c == null) {
			c = getSyncCount();
			countMap.put(KEY_SYNC_COUNT, c);
		}
		
		boolean exceeded = c.intValue() > syncThreshold;
		if (exceeded) {
			if (log.isTraceEnabled()) {
				log.trace("Skipping run to prioritize sync task which has more than " + syncThreshold + " items to process");
			}
		}
		
		return exceeded;
	}
	
	protected long getSyncCount() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching count of items in the sync queue");
		}
		
		long count = syncRepo.count();
		
		if (log.isDebugEnabled()) {
			log.debug("Sync queue count: " + count);
		}
		
		return count;
	}
	
	/**
	 * Gets the logical task name
	 *
	 * @return the task name
	 */
	public abstract String getTaskName();
	
	/**
	 * Subclasses should add their implementation logic in this method
	 * 
	 * @return true if this runnable should stop otherwise false
	 * @throws Exception
	 */
	public abstract boolean doRun() throws Exception;
	
}
