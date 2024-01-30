package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a jms message by moving it to the appropriate queue
 */
@Component("receiverJmsMessageProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverJmsMessageProcessor extends BaseQueueProcessor<JmsMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverJmsMessageProcessor.class);
	
	private ReceiverService service;
	
	public ReceiverJmsMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    ReceiverService service) {
		super(executor);
		this.service = service;
	}
	
	@Override
	public String getProcessorName() {
		return "jms msg processor";
	}
	
	@Override
	public String getQueueName() {
		return "jms-msg-processor";
	}
	
	@Override
	public String getUniqueId(JmsMessage item) {
		if (item.getType() == MessageType.RECONCILIATION) {
			//Process any reconciliation message in parallel
			return item.getId().toString();
		}
		
		//Ensures that messages of this type are processed in serial
		return getLogicalType(item);
	}
	
	@Override
	public String getThreadName(JmsMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public String getLogicalType(JmsMessage item) {
		return item.getType().name();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
	@Override
	public void processItem(JmsMessage item) {
		if (item.getType() == MessageType.SYNC) {
			service.processSyncJmsMessage(item);
		}
	}
	
}
