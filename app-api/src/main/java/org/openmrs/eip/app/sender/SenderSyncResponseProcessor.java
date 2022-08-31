package org.openmrs.eip.app.sender;

import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncResponseProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncResponseProcessor extends BaseSenderQueueProcessor<SenderSyncResponse> {
	
	@Override
	public String getProcessorName() {
		return "sync response";
	}
	
	@Override
	public String getThreadName(SenderSyncResponse response) {
		return response.getMessageUuid() + "-" + response.getId();
	}
	
	@Override
	public String getItemKey(SenderSyncResponse item) {
		return item.getId().toString();
	}
	
	@Override
	public boolean processInParallel(SenderSyncResponse item) {
		return true;
	}
	
	@Override
	public String getQueueName() {
		return "sync-response";
	}
	
	@Override
	public String getDestinationUri() {
		return SenderConstants.URI_RESPONSE_PROCESSOR;
	}
	
}
