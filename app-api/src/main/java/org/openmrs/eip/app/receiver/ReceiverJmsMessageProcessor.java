package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.management.service.ReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a jms message by moving it to the appropriate queue
 */
@Component("receiverJmsMsgProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverJmsMessageProcessor extends BaseQueueProcessor<JmsMessage> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverJmsMessageProcessor.class);
	
	private ReceiverService receiverService;
	
	private ReconcileService reconcileService;
	
	public ReceiverJmsMessageProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    ReceiverService receiverService, ReconcileService reconcileService) {
		super(executor);
		this.receiverService = receiverService;
		this.reconcileService = reconcileService;
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
		if (item.getType() == MessageType.RECONCILE) {
			//Process messages from same site serially
			return item.getSiteId();
		}
		
		//Process all messages serially
		//We currently ignore site because it will be null in prod for existing messages at time of upgrade.
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
			receiverService.processSyncJmsMessage(item);
		} else if (item.getType() == MessageType.RECONCILE) {
			reconcileService.processSyncJmsMessage(item);
		}
	}
	
}
