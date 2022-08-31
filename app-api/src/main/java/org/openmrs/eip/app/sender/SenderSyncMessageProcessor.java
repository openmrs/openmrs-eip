package org.openmrs.eip.app.sender;

import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncMsgProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncMessageProcessor extends BaseSenderQueueProcessor<SenderSyncMessage> {
	
	@Override
	public String getProcessorName() {
		return "sync msg";
	}
	
	@Override
	public String getThreadName(SenderSyncMessage msg) {
		return msg.getTableName() + "-" + msg.getIdentifier() + "-" + msg.getId();
	}
	
	@Override
	public String getItemKey(SenderSyncMessage item) {
		return item.getTableName() + "#" + item.getIdentifier();
	}
	
	@Override
	public boolean processInParallel(SenderSyncMessage item) {
		return item.getSnapshot();
	}
	
	@Override
	public String getQueueName() {
		return "sync-msg";
	}
	
	@Override
	public String getDestinationUri() {
		return SenderConstants.URI_ACTIVEMQ_PUBLISHER;
	}
	
}
