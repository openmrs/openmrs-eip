package org.openmrs.eip.app;

import java.util.Date;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;

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
		JmsMessage msg = new JmsMessage();
		try {
			msg.setSiteId(message.getStringProperty(SyncConstants.JMS_HEADER_SITE));
			msg.setBody(message.getBody(byte[].class));
			msg.setDateCreated(new Date());
			if (LOG.isDebugEnabled()) {
				LOG.debug("Saving received message");
			}
			
			repo.save(msg);
			message.acknowledge();
		}
		catch (Throwable e) {
			LOG.warn("Failed to process incoming message", e);
			AppUtils.shutdown();
		}
	}
	
}
