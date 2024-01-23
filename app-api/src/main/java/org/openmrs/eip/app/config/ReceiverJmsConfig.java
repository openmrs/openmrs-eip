package org.openmrs.eip.app.config;

import org.openmrs.eip.app.ReceiverMessageListener;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;

@Configuration
@Profile(SyncProfiles.RECEIVER)
public class ReceiverJmsConfig {
	
	@Value("${" + ReceiverConstants.PROP_SYNC_QUEUE + "}")
	private String queueName;
	
	@Bean
	public DefaultMessageListenerContainer getListenerContainer(ConnectionFactory cf, ReceiverMessageListener listener) {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(cf);
		container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		container.setDestinationName(queueName);
		container.setMessageListener(listener);
		return container;
	}
	
}
