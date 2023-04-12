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
 * Parent site executor that executes all the child tasks once per run.
 */
public class SiteParentTask extends BaseTask {
	
	protected static final Logger log = LoggerFactory.getLogger(SiteParentTask.class);
	
	private static final int TASK_COUNT = 6;
	
	protected static final String PARENT_TASK_NAME = "parent task";
	
	@Getter
	private final SiteInfo siteInfo;
	
	private final SiteMessageConsumer synchronizer;
	
	private final CacheEvictor evictor;
	
	private final SearchIndexUpdater updater;
	
	private final SyncResponseSender responseSender;
	
	private final SyncedMessageArchiver archiver;
	
	private final SyncedMessageDeleter deleter;
	
	@Getter
	private final ExecutorService childExecutor = Executors.newFixedThreadPool(TASK_COUNT);
	
	public SiteParentTask(SiteInfo siteInfo, ThreadPoolExecutor syncExecutor) {
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
		return getSiteInfo().getName() + " " + PARENT_TASK_NAME;
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Start");
		}
		
		List<CompletableFuture<Void>> futures = synchronizedList(new ArrayList(TASK_COUNT));
		
		futures.add(CompletableFuture.runAsync(synchronizer, childExecutor));
		futures.add(CompletableFuture.runAsync(evictor, childExecutor));
		futures.add(CompletableFuture.runAsync(updater, childExecutor));
		futures.add(CompletableFuture.runAsync(responseSender, childExecutor));
		futures.add(CompletableFuture.runAsync(archiver, childExecutor));
		futures.add(CompletableFuture.runAsync(deleter, childExecutor));
		
		AppUtils.waitForFutures(futures, ReceiverConstants.CHILD_TASK_NAME);
		
		if (log.isTraceEnabled()) {
			log.trace("Stop");
		}
		
		return true;
	}
	
}
