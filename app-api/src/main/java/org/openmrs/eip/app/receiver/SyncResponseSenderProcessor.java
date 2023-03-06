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
 * Processes synced messages to update responseSent field and set it to true and saves the changes
 * to the database.
 */
@Component("syncResponseSenderProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SyncResponseSenderProcessor extends BaseQueueProcessor<SyncedMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncResponseSenderProcessor.class);
	
	private SyncedMessageRepository repo;
	
	public SyncResponseSenderProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SyncedMessageRepository repo) {
		super(executor);
		this.repo = repo;
	}
	
	@Override
	public String getProcessorName() {
		return "response sender";
	}
	
	@Override
	public String getUniqueId(SyncedMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getQueueName() {
		return "response-sender";
	}
	
	@Override
	public String getThreadName(SyncedMessage item) {
		return item.getSite().getIdentifier() + "-" + item.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(item.getModelClassName()) + "-" + item.getIdentifier();
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
		item.setResponseSent(true);
		repo.save(item);
	}
	
}
