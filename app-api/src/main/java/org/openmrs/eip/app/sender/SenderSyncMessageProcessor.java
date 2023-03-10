package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.BaseFromCamelToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.SenderSyncMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.Utils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("senderSyncMsgProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderSyncMessageProcessor extends BaseFromCamelToCamelEndpointProcessor<SenderSyncMessage> {
	
	public SenderSyncMessageProcessor(ProducerTemplate producerTemplate,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor) {
		super(SenderConstants.URI_ACTIVEMQ_PUBLISHER, producerTemplate, executor);
	}
	
	@Override
	public String getProcessorName() {
		return "sync msg";
	}
	
	@Override
	public String getThreadName(SenderSyncMessage msg) {
		return msg.getTableName() + "-" + msg.getIdentifier() + "-" + msg.getMessageUuid();
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
	public String getLogicalType(SenderSyncMessage item) {
		return item.getTableName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return Utils.getListOfTablesInHierarchy(logicalType);
	}
	
}
