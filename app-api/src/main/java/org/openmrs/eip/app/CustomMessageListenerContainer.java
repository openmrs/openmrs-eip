package org.openmrs.eip.app;

import org.apache.camel.component.jms.DefaultJmsMessageListenerContainer;
import org.apache.camel.component.jms.JmsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;

/**
 * Custom {@link org.springframework.jms.listener.MessageListenerContainer} that only acknowledges
 * messages to be removed from active MQ depending on our application logic to ensure no message
 * ever goes unprocessed. This is currently only used by the sender to receive messages.
 */
public class CustomMessageListenerContainer extends DefaultJmsMessageListenerContainer {
	
	protected static final Logger log = LoggerFactory.getLogger(CustomMessageListenerContainer.class);
	
	private static boolean commit = false;
	
	public CustomMessageListenerContainer(JmsEndpoint endpoint) {
		super(endpoint);
	}
	
	public synchronized static void enableAcknowledgement() {
		commit = true;
	}
	
	@Override
	protected void messageReceived(Object invoker, Session session) {
		commit = false;
		if (log.isDebugEnabled()) {
			log.debug("Sync message received, disabled framework message acknowledgement");
		}
		
		super.messageReceived(invoker, session);
	}
	
	@Override
	protected void commitIfNecessary(Session session, Message message) throws JMSException {
		if (message != null && !commit) {
			log.warn("Skipping message acknowledgement possibly due to an encountered error");
			return;
		}
		
		super.commitIfNecessary(session, message);
	}
	
}
