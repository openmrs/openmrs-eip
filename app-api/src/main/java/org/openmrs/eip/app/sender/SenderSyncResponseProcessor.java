package org.openmrs.eip.app.sender;

import java.util.List;

import org.openmrs.eip.app.BaseToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncResponseProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncResponseProcessor extends BaseToCamelEndpointProcessor<SenderSyncResponse> {
	
	@Override
	public String getProcessorName() {
		return "sync response";
	}
	
	@Override
	public String getThreadName(SenderSyncResponse response) {
		return response.getMessageUuid() + "-" + response.getId();
	}
	
	@Override
	public String getUniqueId(SenderSyncResponse item) {
		return item.getId().toString();
	}
	
	@Override
	public String getQueueName() {
		return "sync-response";
	}
	
	@Override
	public String getEndpointUri() {
		return SenderConstants.URI_RESPONSE_PROCESSOR;
	}
	
	@Override
	public String getLogicalType(SenderSyncResponse item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
}
