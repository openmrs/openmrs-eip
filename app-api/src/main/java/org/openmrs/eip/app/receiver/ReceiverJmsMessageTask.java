package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of JmsMessages and submits them to the {@link ReceiverJmsMessageProcessor} for
 * processing.
 */
public class ReceiverJmsMessageTask extends BaseDelegatingQueueTask<JmsMessage, ReceiverJmsMessageProcessor> {
	
	private JmsMessageRepository repo;
	
	public ReceiverJmsMessageTask() {
		super(SyncContext.getBean(ReceiverJmsMessageProcessor.class));
		this.repo = SyncContext.getBean(JmsMessageRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "jms msg task";
	}
	
	@Override
	public List<JmsMessage> getNextBatch() {
		return repo.findAll(AppUtils.getTaskPage()).getContent();
	}
	
}
