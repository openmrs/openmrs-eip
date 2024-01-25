package org.openmrs.eip.app.receiver;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverMessageListener implements MessageListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverMessageListener.class);
	
	private JmsMessageRepository repo;
	
	public ReceiverMessageListener(JmsMessageRepository repo) {
		this.repo = repo;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			JmsMessage msg = new JmsMessage();
			byte[] body;
			if (message instanceof TextMessage) {
				body = message.getBody(String.class).getBytes(StandardCharsets.UTF_8);
			} else {
				body = message.getBody(byte[].class);
			}
			
			msg.setBody(body);
			msg.setSiteId(message.getStringProperty(SyncConstants.JMS_HEADER_SITE));
			MessageType type = MessageType.SYNC;
			String typeStr = message.getStringProperty(SyncConstants.JMS_HEADER_TYPE);
			if (StringUtils.isNotBlank(typeStr)) {
				type = MessageType.valueOf(typeStr);
			}
			
			msg.setType(type);
			msg.setDateCreated(new Date());
			if (LOG.isDebugEnabled()) {
				LOG.debug("Saving received JMS message");
			}
			
			repo.save(msg);
		}
		catch (Throwable t) {
			throw new EIPException("Failed to process incoming JMS message", t);
		}
	}
	
}
