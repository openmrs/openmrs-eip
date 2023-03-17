package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of synced messages where the responses are sent and the outcome is set to error or
 * conflict, forwards them to the {@link SyncedMessageDeletingProcessor}.
 */
public class SyncedMessageDeleter extends BasePostSyncActionRunnable {
	
	private SyncedMessageDeletingProcessor processor;
	
	public SyncedMessageDeleter(SiteInfo site) {
		super(site);
		processor = SyncContext.getBean(SyncedMessageDeletingProcessor.class);
	}
	
	@Override
	public String getTaskName() {
		return "synced msg deleter task";
	}
	
	@Override
	public void process(List<SyncedMessage> messages) throws Exception {
		processor.processWork(messages);
	}
	
	@Override
	public List<SyncedMessage> getNextBatch() {
		return repo.getBatchOfMessagesForDeleting(site, page);
	}
	
}
