package org.openmrs.eip.app.sender;

import java.util.List;

import org.apache.camel.spi.annotations.Component;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;

import jakarta.jms.ConnectionFactory;

@Component("senderSyncBatchManager")
public class SenderSyncBatchManager extends BaseSyncBatchManager<SenderSyncMessage, SyncModel> {
	
	@Value("db-sync.senderId")
	private String senderId;
	
	@Value("camel.output.endpoint")
	private String brokerEndpoint;
	
	private String queueName;
	
	public SenderSyncBatchManager(int batchSize, ConnectionFactory connectionFactory) {
		super(batchSize, connectionFactory);
	}
	
	@Override
	protected String getQueueName() {
		if (queueName == null) {
			if (!brokerEndpoint.startsWith("activemq:")) {
				throw new EIPException(brokerEndpoint + " is an invalid broker endpoint value");
			}
			
			queueName = brokerEndpoint.substring(brokerEndpoint.indexOf(":") + 1);
		}
		
		return queueName;
	}
	
	@Override
	protected SyncModel convert(SenderSyncMessage message) {
		SyncModel syncModel = JsonUtils.unmarshalSyncModel(message.getData());
		syncModel.getMetadata().setSourceIdentifier(senderId);
		return syncModel;
	}
	
	@Override
	protected void updateItems(List<Long> messageIds) {
		String myIds = StringUtils.join(messageIds, ",");
		CamelUtils.send("sql:UPDATE sender_sync_message SET status = 'SENT', date_sent = now() WHERE id IN (" + myIds
		        + ")?dataSource=#" + SyncConstants.MGT_DATASOURCE_NAME);
	}
	
}
