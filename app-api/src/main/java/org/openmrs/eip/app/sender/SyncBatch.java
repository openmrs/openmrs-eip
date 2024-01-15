package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;

public class SyncBatch {
	
	private static final Logger LOG = LoggerFactory.getLogger(SyncBatch.class);
	
	private static String senderId;
	
	private static String queueName;
	
	private static int batchSize;
	
	private static List<SyncModel> items;
	
	private static List<Long> itemIds;
	
	private SyncBatch() {
	}
	
	public static SyncBatch getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	public void reset() {
		getItems().clear();
		getItemIds().clear();
	}
	
	public void add(SenderSyncMessage message) {
		SyncModel model = JsonUtils.unmarshalSyncModel(message.getData());
		model.getMetadata().setSourceIdentifier(senderId);
		model.getMetadata().setDateSent(LocalDateTime.now());
		getItems().add(model);
		getItemIds().add(message.getId());
	}
	
	public void send() throws JMSException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending " + items.size() + " sync messages(s)");
		}
		
		ConnectionFactory cf = SyncContext.getBean(ConnectionFactory.class);
		try (Connection conn = cf.createConnection(); Session session = conn.createSession()) {
			Queue queue = session.createQueue(queueName);
			try (MessageProducer p = session.createProducer(queue)) {
				//TODO Zip and send ByteMessage instead
				p.send(session.createTextMessage(JsonUtils.marshall(getItems())));
			}
		}
		
		LOG.info("Successfully sent a sync batch of " + getItems().size() + " sync messages");
		
		String myIds = StringUtils.join(getItemIds(), ",");
		CamelUtils.send("sql:UPDATE sender_sync_message set status = 'SENT', date_sent = now() where id in (" + myIds
		        + ")?dataSource=#" + MGT_DATASOURCE_NAME);
		
		LOG.info("Successfully updated the statuses of " + getItemIds().size() + " sync messages");
		
		reset();
	}
	
	private List<SyncModel> getItems() {
		if (items == null) {
			items = Collections.synchronizedList(new ArrayList<>(batchSize));
		}
		
		return items;
	}
	
	private List<Long> getItemIds() {
		if (itemIds == null) {
			itemIds = Collections.synchronizedList(new ArrayList<>(batchSize));
		}
		
		return itemIds;
	}
	
	private static final class InstanceHolder {
		
		private static final SyncBatch INSTANCE = new SyncBatch();
		
	}
	
}
