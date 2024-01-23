package org.openmrs.eip.app;

import java.util.Date;

import org.openmrs.eip.app.management.entity.receiver.BaseJmsMessage;
import org.openmrs.eip.app.management.entity.receiver.BytesJmsMessage;
import org.openmrs.eip.app.management.entity.receiver.TextJmsMessage;
import org.openmrs.eip.app.management.repository.BytesMessageRepository;
import org.openmrs.eip.app.management.repository.TextMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@Component
@Profile(SyncProfiles.RECEIVER)
public class ReceiverMessageListener implements MessageListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverMessageListener.class);
	
	private TextMessageRepository txtMsgRepo;
	
	private BytesMessageRepository bytesMsgRepo;
	
	public ReceiverMessageListener(TextMessageRepository txtMsgRepo, BytesMessageRepository bytesMsgRepo) {
		this.txtMsgRepo = txtMsgRepo;
		this.bytesMsgRepo = bytesMsgRepo;
	}
	
	@Override
	public void onMessage(Message message) {
		try {
			JpaRepository repo;
			BaseJmsMessage msg;
			if (message instanceof TextMessage) {
				repo = txtMsgRepo;
				msg = new TextJmsMessage();
			} else {
				repo = bytesMsgRepo;
				msg = new BytesJmsMessage();
				msg.setBody(message.getBody(byte[].class));
			}
			
			msg.setSiteId(message.getStringProperty(SyncConstants.JMS_HEADER_SITE));
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
