package org.openmrs.eip.app.receiver;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseTask;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.receiver.task.Synchronizer;
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
	
	private Synchronizer synchronizer;
	
	private CacheEvictor evictor;
	
	private SearchIndexUpdater updater;
	
	private SyncResponseSender responseSender;
	
	private SyncedMessageArchiver archiver;
	
	private SyncedMessageDeleter deleter;
	
	@Getter
	private final ExecutorService childExecutor = Executors.newFixedThreadPool(TASK_COUNT);
	
	public SiteParentTask(SiteInfo siteInfo, List<Class<? extends Runnable>> disabledTaskClasses) {
		this.siteInfo = siteInfo;
		if (!disabledTaskClasses.contains(Synchronizer.class)) {
			synchronizer = new Synchronizer(siteInfo);
		}
		
		if (!disabledTaskClasses.contains(CacheEvictor.class)) {
			evictor = new CacheEvictor(siteInfo);
		}
		
		if (!disabledTaskClasses.contains(SearchIndexUpdater.class)) {
			updater = new SearchIndexUpdater(siteInfo);
		}
		
		if (!disabledTaskClasses.contains(SyncResponseSender.class)) {
			responseSender = new SyncResponseSender(siteInfo);
		}
		
		if (!disabledTaskClasses.contains(SyncedMessageArchiver.class)) {
			archiver = new SyncedMessageArchiver(siteInfo);
		}
		
		if (!disabledTaskClasses.contains(SyncedMessageDeleter.class)) {
			deleter = new SyncedMessageDeleter(siteInfo);
		}
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
		
		final List<CompletableFuture<Void>> futures = synchronizedList(new ArrayList(TASK_COUNT));
		runTask(synchronizer, futures);
		runTask(evictor, futures);
		runTask(updater, futures);
		runTask(responseSender, futures);
		runTask(archiver, futures);
		runTask(deleter, futures);
		
		AppUtils.waitForFutures(futures, ReceiverConstants.CHILD_TASK_NAME);
		
		if (log.isTraceEnabled()) {
			log.trace("Stop");
		}
		
		return true;
	}
	
	private void runTask(Runnable task, List<CompletableFuture<Void>> futures) {
		if (task != null) {
			futures.add(CompletableFuture.runAsync(task, childExecutor));
		}
	}
	
}
