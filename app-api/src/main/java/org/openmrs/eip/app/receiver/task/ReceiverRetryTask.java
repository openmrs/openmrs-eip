package org.openmrs.eip.app.receiver.task;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.receiver.ReceiverUtils;
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
	
	private static final Set<String> FAILED_ENTITIES = Collections.synchronizedSet(new HashSet<>());
	
	protected static final int BATCH_SIZE = 200;
	
	public ReceiverRetryTask(ReceiverRetryProcessor processor) {
		super(processor);
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
		FAILED_ENTITIES.clear();//Clear just to be extra sure
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Loaded {} retry ids", retryIds.size());
		}
	}
	
	public Set<String> getFailedEntities() {
		return Collections.unmodifiableSet(FAILED_ENTITIES);
	}
	
	public void postProcess(ReceiverRetryQueueItem retry, boolean errorEncountered) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Removing id {}", retry.getId());
		}
		
		if (errorEncountered) {
			String modelClass = ReceiverUtils.getEffectiveModelClassName(retry.getModelClassName());
			FAILED_ENTITIES.add(modelClass + "#" + retry.getIdentifier());
		}
		
		retryIds.remove(retry.getId());
	}
	
	@Override
	public void beforeStop() {
		if (!retryIds.isEmpty()) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("Clearing retry ids");
			}
			
			retryIds.clear();
		}
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Clearing failed entity list");
		}
		
		FAILED_ENTITIES.clear();
	}
	
	@Override
	public List<ReceiverRetryQueueItem> getNextBatch() {
		if (retryIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<Long> ids = retryIds;
		if (retryIds.size() > BATCH_SIZE) {
			ids = retryIds.subList(0, BATCH_SIZE);
		}
		
		return repo.getByIdInOrderByDateReceivedAsc(ids);
	}
	
}
