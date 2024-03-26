package org.openmrs.eip.app.receiver.task;

import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.receiver.processor.ReceiverRetryProcessor;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of receiver retry items and forwards them to the {@link ReceiverRetryProcessor}.
 */
public class ReceiverRetryTask extends BaseDelegatingQueueTask<ReceiverRetryQueueItem, ReceiverRetryProcessor> {
	
	private ReceiverRetryRepository repo;
	
	public ReceiverRetryTask() {
		super(SyncContext.getBean(ReceiverRetryProcessor.class));
		this.repo = SyncContext.getBean(ReceiverRetryRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "retry task";
	}
	
	@Override
	public List<ReceiverRetryQueueItem> getNextBatch() {
		return repo.getRetries(AppUtils.getTaskPage());
	}
	
}
