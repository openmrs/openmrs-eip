package org.openmrs.eip.app.receiver;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseTask;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/**
 * Executes all the tasks for the associated site once in each run.
 */
public class SiteExecutorTask extends BaseTask {
	
	protected static final Logger log = LoggerFactory.getLogger(SiteExecutorTask.class);
	
	private static final String TASK_NAME = "site executor task";
	
	private static final int TASK_COUNT = 6;
	
	@Getter
	private final SiteInfo siteInfo;
	
	private final SiteMessageConsumer synchronizer;
	
	private final CacheEvictor evictor;
	
	private final SearchIndexUpdater updater;
	
	private final SyncResponseSender responseSender;
	
	private final SyncedMessageArchiver archiver;
	
	private final SyncedMessageDeleter deleter;
	
	private final ExecutorService taskExecutor = Executors.newFixedThreadPool(TASK_COUNT);
	
	public SiteExecutorTask(SiteInfo siteInfo, ThreadPoolExecutor syncExecutor) {
		this.siteInfo = siteInfo;
		synchronizer = new SiteMessageConsumer(ReceiverConstants.URI_MSG_PROCESSOR, siteInfo, syncExecutor);
		evictor = new CacheEvictor(siteInfo);
		updater = new SearchIndexUpdater(siteInfo);
		responseSender = new SyncResponseSender(siteInfo);
		archiver = new SyncedMessageArchiver(siteInfo);
		deleter = new SyncedMessageDeleter(siteInfo);
	}
	
	@Override
	public String getTaskName() {
		return getSiteInfo().getName() + TASK_NAME;
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Start");
		}
		
		List<CompletableFuture<Void>> futures = synchronizedList(new ArrayList(TASK_COUNT));
		
		futures.add(CompletableFuture.runAsync(synchronizer, taskExecutor));
		futures.add(CompletableFuture.runAsync(evictor, taskExecutor));
		futures.add(CompletableFuture.runAsync(updater, taskExecutor));
		futures.add(CompletableFuture.runAsync(responseSender, taskExecutor));
		futures.add(CompletableFuture.runAsync(archiver, taskExecutor));
		futures.add(CompletableFuture.runAsync(deleter, taskExecutor));
		
		AppUtils.waitForFutures(futures, TASK_NAME);
		
		if (log.isTraceEnabled()) {
			log.trace("Stop");
		}
		
		return true;
	}
}
