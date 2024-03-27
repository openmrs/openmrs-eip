package org.openmrs.eip.app.receiver.task;

import java.util.Collections;
import java.util.List;

import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.receiver.processor.ReceiverRetryProcessor;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a batch of receiver retry items and forwards them to the {@link ReceiverRetryProcessor}.
 */
public class ReceiverRetryTask extends BaseDelegatingQueueTask<ReceiverRetryQueueItem, ReceiverRetryProcessor> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverRetryTask.class);
	
	private ReceiverRetryRepository repo;
	
	private static List<Long> retryIds;
	
	public static final int BATCH_SIZE = 200;
	
	public ReceiverRetryTask() {
		super(SyncContext.getBean(ReceiverRetryProcessor.class));
		this.repo = SyncContext.getBean(ReceiverRetryRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "retry task";
	}
	
	@Override
	public void beforeStart() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Loading retry ids");
		}
		
		retryIds = Collections.synchronizedList(repo.getIds());
	}
	
	public void postProcess(Long retryId) {
		retryIds.remove(retryId);
	}
	
	@Override
	public void beforeStop() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Clearing retry ids");
		}
		
		retryIds.clear();
	}
	
	@Override
	public List<ReceiverRetryQueueItem> getNextBatch() {
		if (retryIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		return repo.getByIdInOrderByDateReceivedAsc(retryIds.subList(0, BATCH_SIZE));
	}
	
}
