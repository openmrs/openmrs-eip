package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.RetryCacheEvictingProcessor;
import org.openmrs.eip.app.receiver.RetrySearchIndexUpdatingProcessor;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Re-processes a receiver retry item.
 */
@Component("receiverRetryProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverRetryProcessor extends BaseSyncProcessor<ReceiverRetryQueueItem> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverRetryProcessor.class);
	
	private ReceiverService service;
	
	private ReceiverRetryRepository retryRepo;
	
	private RetryCacheEvictingProcessor evictProcessor;
	
	private RetrySearchIndexUpdatingProcessor indexProcessor;
	
	public ReceiverRetryProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    SyncHelper syncHelper, ReceiverRetryRepository retryRepo, RetryCacheEvictingProcessor evictProcessor,
	    RetrySearchIndexUpdatingProcessor indexProcessor) {
		super(executor, syncHelper);
		this.service = service;
		this.retryRepo = retryRepo;
		this.evictProcessor = evictProcessor;
		this.indexProcessor = indexProcessor;
	}
	
	@Override
	public String getProcessorName() {
		return "retry";
	}
	
	@Override
	public String getQueueName() {
		return "retry";
	}
	
	@Override
	public String getUniqueId(ReceiverRetryQueueItem item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getThreadName(ReceiverRetryQueueItem msg) {
		return msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-"
		        + msg.getIdentifier() + "-" + msg.getMessageUuid();
	}
	
	@Override
	public String getLogicalType(ReceiverRetryQueueItem item) {
		return item.getModelClassName();
	}
	
	@Override
	protected void beforeSync(ReceiverRetryQueueItem retry) {
		LOG.info("Re-processing message");
		retry.setAttemptCount(retry.getAttemptCount() + 1);
	}
	
	@Override
	protected String getSyncPayload(ReceiverRetryQueueItem retry) {
		return retry.getEntityPayload();
	}
	
	@Override
	protected void afterSync(ReceiverRetryQueueItem retry) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Done re-processing message");
		}
		
		evictProcessor.process(retry);
		indexProcessor.process(retry);
		service.archiveRetry(retry);
	}
	
	@Override
	protected void onConflict(ReceiverRetryQueueItem retry) {
		service.moveToConflictQueue(retry);
	}
	
	@Override
	protected void onError(ReceiverRetryQueueItem retry, String exceptionClass, String errorMsg) {
		retry.setExceptionType(exceptionClass);
		retry.setMessage(errorMsg);
		retry.setDateChanged(new Date());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving updates for retry item");
		}
		
		retryRepo.save(retry);
		
		if (LOG.isTraceEnabled()) {
			LOG.trace("Successfully updated retry item");
		}
	}
	
}
