package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("receiverArchivingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverArchivingProcessor extends BaseQueueProcessor<SyncMessage> {
	
	@Override
	public String getProcessorName() {
		return "receiver archive";
	}
	
	@Override
	public String getItemKey(SyncMessage item) {
		return item.getId().toString();
	}
	
	@Override
	public boolean processInParallel(SyncMessage item) {
		return true;
	}
	
	@Override
	public String getQueueName() {
		return "receiver-archive";
	}
	
	@Override
	public String getThreadName(SyncMessage item) {
		return item.getModelClassName() + "-" + item.getIdentifier() + "-" + item.getMessageUuid() + "-" + item.getId();
	}
	
	@Override
	public String getDestinationUri() {
		return null;
	}
	
}
