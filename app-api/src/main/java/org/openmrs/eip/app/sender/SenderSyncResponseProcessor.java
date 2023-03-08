package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.BaseFromCamelToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.SenderSyncResponse;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncResponseProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncResponseProcessor extends BaseFromCamelToCamelEndpointProcessor<SenderSyncResponse> {
	
	public SenderSyncResponseProcessor(ProducerTemplate producerTemplate,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor) {
		super(SenderConstants.URI_RESPONSE_PROCESSOR, producerTemplate, executor);
	}
	
	@Override
	public String getProcessorName() {
		return "sync response";
	}
	
	@Override
	public String getThreadName(SenderSyncResponse response) {
		return response.getMessageUuid();
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
	public String getLogicalType(SenderSyncResponse item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
}
