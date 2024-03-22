package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.service.ReceiverService;
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
	
	public ReceiverRetryProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    SyncHelper syncHelper) {
		super(executor, syncHelper);
		this.service = service;
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
		//TODO Add logic
	}
	
	@Override
	protected String getSyncPayload(ReceiverRetryQueueItem retry) {
		return retry.getEntityPayload();
	}
	
	@Override
	protected void afterSync(ReceiverRetryQueueItem item) {
		//TODO add post sync logic
		if (LOG.isDebugEnabled()) {
			LOG.debug("Done re-processing message");
		}
	}
	
	@Override
	protected void onConflict(ReceiverRetryQueueItem retry) {
		service.moveToConflictQueue(retry);
	}
	
	@Override
	protected void onError(ReceiverRetryQueueItem retry, Throwable throwable) {
		//TODO Updated retry item
	}
	
}
