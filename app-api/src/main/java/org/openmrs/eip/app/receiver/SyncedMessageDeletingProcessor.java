package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Deletes synced messages where the responses are sent and the outcome is set to error or conflict
 */
@Component("syncedMsgDeletingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncedMessageDeletingProcessor extends BaseQueueProcessor<SyncedMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncedMessageDeletingProcessor.class);
	
	private SyncedMessageRepository repo;
	
	public SyncedMessageDeletingProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SyncedMessageRepository repo) {
		super(executor);
		this.repo = repo;
	}
	
	@Override
	public String getProcessorName() {
		return "msg deleter";
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getQueueName() {
		return "msg-deleter";
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(item.getModelClassName()) + "-"
		        + item.getIdentifier() + "-" + item.getMessageUuid();
	}
	
	@Override
	public String getLogicalType(SyncedMessage item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
	@Override
	public void processItem(SyncedMessage item) {
		if (log.isDebugEnabled()) {
			log.debug("Deleting synced message with outcome: " + item.getOutcome());
		}
		
		repo.delete(item);
		
		if (log.isDebugEnabled()) {
			log.debug("Successfully deleted synced message");
		}
	}
	
}
