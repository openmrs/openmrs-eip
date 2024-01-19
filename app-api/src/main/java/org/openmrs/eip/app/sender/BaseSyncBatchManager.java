package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_LARGE_MSG_SIZE;
import static org.openmrs.eip.app.SyncConstants.PROP_LARGE_MSG_SIZE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.utils.JsonUtils;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.StreamMessage;

public abstract class BaseSyncBatchManager<I extends AbstractEntity, O> {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseSyncBatchManager.class);
	
	@Value("${" + PROP_LARGE_MSG_SIZE + ":" + DEFAULT_LARGE_MSG_SIZE + "}")
	private int largeMsgSize;
	
	private List<O> items;
	
	private List<Long> itemIds;
	
	private ConnectionFactory connectionFactory;
	
	public BaseSyncBatchManager(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	
	/**
	 * Clears all the batch contents.
	 */
	public void reset() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resetting batch");
		}
		
		getItems().clear();
		getItemIds().clear();
	}
	
	/**
	 * Adds the item to the batch
	 * 
	 * @param item the item to add
	 */
	public void add(I item) {
		getItems().add(convert(item));
		getItemIds().add(item.getId());
	}
	
	/**
	 * Sends all the messages contained in the batch to the message broker.
	 * 
	 * @throws JMSException
	 */
	public void send() throws JMSException, IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending batch of " + items.size() + " items(s)");
		}
		
		//TODO Reuse Session and MessageProducer
		try (Connection conn = connectionFactory.createConnection(); Session session = conn.createSession()) {
			Queue queue = session.createQueue(getQueueName());
			try (MessageProducer p = session.createProducer(queue)) {
				//TODO Exclude JMSMessageId and timestamp by disabling them
				byte[] bytes = JsonUtils.marshalToBytes(getItems());
				Message msg;
				if (bytes.length < largeMsgSize) {
					BytesMessage bytesMsg = session.createBytesMessage();
					bytesMsg.writeBytes(bytes);
					msg = bytesMsg;
				} else {
					byte[] compressedBytes = Utils.compress(bytes);
					if (compressedBytes.length < largeMsgSize) {
						BytesMessage bytesMsg = session.createBytesMessage();
						bytesMsg.writeBytes(compressedBytes);
						msg = bytesMsg;
					} else {
						StreamMessage streamMsg = session.createStreamMessage();
						streamMsg.writeBytes(compressedBytes);
						msg = streamMsg;
					}
				}
				
				msg.setIntProperty(SyncConstants.SYNC_BATCH_PROP_SIZE, getItems().size());
				p.send(msg);
			}
		}
		
		LOG.info("Successfully sent a sync batch of " + getItems().size() + " item(s)");
		
		updateItems(getItemIds());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Successfully updated " + getItemIds().size() + " items(s)");
		}
		
		reset();
	}
	
	/**
	 * Gets the queue name to send to in the message broker.
	 * 
	 * @return the queue name
	 */
	protected abstract String getQueueName();
	
	/**
	 * Gets the maximum batch size to send to the message broker.
	 * 
	 * @return batch size
	 */
	protected abstract int getBatchSize();
	
	/**
	 * Converts the item to a serializable instance.
	 * 
	 * @param item the item to convert
	 * @return the converted object
	 */
	protected abstract O convert(I item);
	
	/**
	 * Updates any relevant fields for the entities matching the specified ids.
	 * 
	 * @param itemIds the entity ids
	 */
	protected abstract void updateItems(List<Long> itemIds);
	
	private List<O> getItems() {
		if (items == null) {
			synchronized (this) {
				if (items == null) {
					items = Collections.synchronizedList(new ArrayList<>(getBatchSize()));
				}
			}
		}
		
		return items;
	}
	
	private List<Long> getItemIds() {
		if (itemIds == null) {
			synchronized (this) {
				if (itemIds == null) {
					itemIds = Collections.synchronizedList(new ArrayList<>(getBatchSize()));
				}
			}
		}
		
		return itemIds;
	}
	
}
