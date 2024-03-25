package org.openmrs.eip.app.receiver.processor;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.SyncHelper;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Synchronizes a SyncMessage payload to the receiver database.
 */
@Component("syncMessageProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncMessageProcessor extends BaseSyncProcessor<SyncMessage> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SyncMessageProcessor.class);
	
	private ReceiverService service;
	
	public SyncMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, ReceiverService service,
	    SyncHelper syncHelper) {
		super(executor, syncHelper);
		this.service = service;
	}
	
	@Override
	public String getProcessorName() {
		return "sync";
	}
	
	@Override
	public String getQueueName() {
		return "sync";
	}
	
	@Override
	public String getUniqueId(SyncMessage msg) {
		return msg.getIdentifier();
	}
	
	@Override
	public String getThreadName(SyncMessage msg) {
		return msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-"
		        + msg.getIdentifier() + "-" + msg.getMessageUuid();
	}
	
	@Override
	public String getLogicalType(SyncMessage msg) {
		return msg.getModelClassName();
	}
	
	@Override
	protected void beforeSync(SyncMessage msg) {
		LOG.info("Processing message");
		//Ensure there is no retry items in the queue for this entity so that changes in messages that happened later
		// don't overwrite those that happened before them.
		if (service.hasRetryItem(msg.getIdentifier(), msg.getModelClassName())) {
			throw new EIPException("Entity still has earlier items in the retry queue");
		}
	}
	
	@Override
	protected String getSyncPayload(SyncMessage msg) {
		return msg.getEntityPayload();
	}
	
	@Override
	protected void afterSync(SyncMessage msg) {
		service.moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
		LOG.info("Done processing message");
	}
	
	@Override
	protected void onConflict(SyncMessage msg) {
		service.processConflictedSyncItem(msg);
	}
	
	@Override
	protected void onError(SyncMessage msg, String exceptionClass, String errorMsg) {
		service.processFailedSyncItem(msg, exceptionClass, errorMsg);
	}
	
}
