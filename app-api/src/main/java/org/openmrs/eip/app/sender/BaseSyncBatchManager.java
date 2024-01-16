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
	
	private int batchSize;
	
	private String queueName;
	
	private List<O> items;
	
	private List<Long> itemIds;
	
	private ConnectionFactory connectionFactory;
	
	public BaseSyncBatchManager(String queueName, int batchSize, ConnectionFactory connectionFactory) {
		this.queueName = queueName;
		this.batchSize = batchSize;
		this.connectionFactory = connectionFactory;
	}
	
	public void reset() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Resetting batch");
		}
		
		getItems().clear();
		getItemIds().clear();
	}
	
	public void add(I item) {
		getItems().add(convert(item));
		getItemIds().add(item.getId());
	}
	
	public void send() throws JMSException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sending batch of " + items.size() + " items(s)");
		}
		
		try (Connection conn = connectionFactory.createConnection(); Session session = conn.createSession()) {
			Queue queue = session.createQueue(queueName);
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
	
	protected abstract O convert(I item);
	
	protected abstract void updateItems(List<Long> itemIds);
	
	private List<O> getItems() {
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
	
}
