package org.openmrs.eip.app.sender.task;

import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.app.sender.SenderSyncMessageProcessor;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of sender sync messages with status NEW and submits them to the
 * {@link SenderSyncMessageProcessor}.
 */
public class SenderSyncMessageTask extends BaseDelegatingQueueTask<SenderSyncMessage, SenderSyncMessageProcessor> {
	
	private SenderSyncMessageRepository repo;
	
	public SenderSyncMessageTask() {
		super(SyncContext.getBean(SenderSyncMessageProcessor.class));
		this.repo = SyncContext.getBean(SenderSyncMessageRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "sync msg task";
	}
	
	@Override
	public List<SenderSyncMessage> getNextBatch() {
		return repo.getNewSyncMessages(AppUtils.getTaskPage());
	}
	
}
