package org.openmrs.eip.app.sender;

import java.util.List;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncMsgProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncMessageProcessor extends BaseQueueProcessor<SenderSyncMessage> {
	
	@Override
	public String getProcessorName() {
		return "sync msg";
	}
	
	@Override
	public String getThreadName(SenderSyncMessage msg) {
		return msg.getTableName() + "-" + msg.getIdentifier() + "-" + msg.getId();
	}
	
	@Override
	public String getUniqueId(SenderSyncMessage item) {
		return item.getIdentifier();
	}
	
	@Override
	public String getQueueName() {
		return "sync-msg";
	}
	
	@Override
	public String getDestinationUri() {
		return SenderConstants.URI_ACTIVEMQ_PUBLISHER;
	}
	
	@Override
	public boolean waitForTasksIndefinitely() {
		return true;
	}
	
	@Override
	public String getLogicalType(SenderSyncMessage item) {
		return item.getTableName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfTablesInHierarchy(logicalType);
	}
	
}
