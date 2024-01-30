package org.openmrs.eip.app;

import org.apache.camel.component.jms.JmsEndpoint;
import org.apache.camel.component.jms.MessageListenerContainerFactory;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * Custom factory class for {@link CustomMessageListenerContainer} instances
 */
@Component("customMessageListenerContainerFactory")
@Profile(SyncProfiles.SENDER)
public class CustomMessageListenerContainerFactory implements MessageListenerContainerFactory {
	
	protected static final Logger log = LoggerFactory.getLogger(CustomMessageListenerContainerFactory.class);
	
	@Override
	public AbstractMessageListenerContainer createMessageListenerContainer(JmsEndpoint endpoint) {
		if (log.isDebugEnabled()) {
			log.debug("Creating CustomMessageListenerContainerFactory");
		}
		
		return new CustomMessageListenerContainer(endpoint);
	}
	
}
