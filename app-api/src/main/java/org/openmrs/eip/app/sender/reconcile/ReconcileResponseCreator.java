package org.openmrs.eip.app.sender.reconcile;

import java.util.UUID;

import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.springframework.jms.core.MessageCreator;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import lombok.Getter;

/**
 * {@link MessageCreator} implementation for reconciliation responses.
 */
public class ReconcileResponseCreator implements MessageCreator {
	
	@Getter
	private String body;
	
	@Getter
	private String siteId;
	
	ReconcileResponseCreator(String body, String siteId) {
		this.body = body;
		this.siteId = siteId;
	}
	
	@Override
	public Message createMessage(Session session) throws JMSException {
		//TODO First compress payload if necessary
		TextMessage message = session.createTextMessage(body);
		message.setStringProperty(SyncConstants.JMS_HEADER_MSG_ID, UUID.randomUUID().toString());
		message.setStringProperty(SyncConstants.JMS_HEADER_SITE, siteId);
		message.setStringProperty(SyncConstants.JMS_HEADER_TYPE, MessageType.RECONCILE.name());
		return message;
	}
}
