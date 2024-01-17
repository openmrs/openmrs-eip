package org.openmrs.eip.app.sender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;

public abstract class BaseSyncBatchManager<I extends AbstractEntity, O> {
	
	private static final Logger LOG = LoggerFactory.getLogger(BaseSyncBatchManager.class);
	
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
	public void send() throws JMSException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending batch of " + items.size() + " items(s)");
		}
		
		try (Connection conn = connectionFactory.createConnection(); Session session = conn.createSession()) {
			Queue queue = session.createQueue(getQueueName());
			try (MessageProducer p = session.createProducer(queue)) {
				//TODO Zip and send ByteMessage instead
				p.send(session.createTextMessage(JsonUtils.marshall(getItems())));
			}
		}
		
		LOG.info("Successfully sent a sync batch of " + getItems().size() + " items");
		
		updateItems(getItemIds());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Successfully updated " + getItemIds().size() + " items");
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
