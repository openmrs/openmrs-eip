package org.openmrs.eip.app.receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

/**
 * Custom {@link org.springframework.jms.listener.MessageListenerContainer} that only acknowledges
 * messages to be removed from active MQ depending on our application logic to ensure no message is
 * ever lost.
 */
public class ReceiverMessageListenerContainer extends DefaultMessageListenerContainer {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverMessageListenerContainer.class);
	
	private static boolean commit = false;
	
	public synchronized static void enableAcknowledgement() {
		commit = true;
	}
	
	@Override
	protected void messageReceived(Object invoker, Session session) {
		commit = false;
		if (LOG.isDebugEnabled()) {
			LOG.debug("Sync message received, disabled framework message acknowledgement");
		}
		
		super.messageReceived(invoker, session);
	}
	
	@Override
	protected void commitIfNecessary(Session session, Message message) throws JMSException {
		if (message != null && !commit) {
			LOG.warn("Skipping message acknowledgement possibly due to an encountered error");
			return;
		}
		
		super.commitIfNecessary(session, message);
	}
	
}
