package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of messages in the synced queue that require OpenMRS cache eviction and forwards
 * them to the {@link CacheEvictingProcessor}.
 */
public class CacheEvictor extends BasePostSyncActionRunnable {
	
	private CacheEvictingProcessor processor;
	
	public CacheEvictor(SiteInfo site) {
		super(site);
		processor = SyncContext.getBean(CacheEvictingProcessor.class);
	}
	
	@Override
	public String getTaskName() {
		return "cache evictor task";
	}
	
	@Override
	public void process(List<SyncedMessage> messages) throws Exception {
		processor.processWork(messages);
	}
	
	@Override
	public List<SyncedMessage> getNextBatch() {
		return repo.getBatchOfMessagesForEviction(site, page);
	}
	
}
