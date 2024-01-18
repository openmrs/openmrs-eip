package org.openmrs.eip.app.sender;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.jms.ConnectionFactory;

@Component("senderSyncBatchManager")
public class SenderSyncBatchManager extends BaseSyncBatchManager<SenderSyncMessage, SyncModel> {
	
	protected static final int DEFAULT_BATCH_SIZE = 200;
	
	@Value("${" + SenderConstants.PROP_SENDER_ID + "}")
	private String senderId;
	
	@Value("${" + SenderConstants.PROP_ACTIVEMQ_ENDPOINT + "}")
	private String brokerEndpoint;
	
	@Value("${" + SenderConstants.PROP_JMS_SEND_BATCH_SIZE + ":" + DEFAULT_BATCH_SIZE + "}")
	private int batchSize;
	
	private String queueName;
	
	public SenderSyncBatchManager(ConnectionFactory connectionFactory) {
		super(connectionFactory);
	}
	
	@Override
	protected int getBatchSize() {
		return batchSize;
	}
	
	@Override
	protected int getItemSize() {
		//Sync messages are estimated to be around 1KiB based data in testing.
		return 1024;
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
