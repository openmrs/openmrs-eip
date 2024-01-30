package org.openmrs.eip.app.config;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.receiver.ReceiverMessageListener;
import org.openmrs.eip.app.receiver.ReceiverMessageListenerContainer;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.util.ErrorHandler;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;

@Profile(SyncProfiles.RECEIVER)
public class ReceiverJmsConfig {
	
	@Value("${" + ReceiverConstants.PROP_SYNC_QUEUE + "}")
	private String queueName;
	
	@Bean
	public DefaultMessageListenerContainer getListenerContainer(ConnectionFactory cf, ReceiverMessageListener listener) {
		DefaultMessageListenerContainer container = new ReceiverMessageListenerContainer();
		container.setConnectionFactory(cf);
		container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		container.setDestinationName(queueName);
		container.setMessageListener(listener);
		container.setErrorHandler(new ErrorHandler() {
			
			private static final Logger LOG = LoggerFactory.getLogger("JmsErrorHandler");
			
			@Override
			public void handleError(Throwable t) {
				LOG.warn("Encountered unhandled JMS error", t);
				AppUtils.shutdown();
			}
			
		});
		
		return container;
	}
	
}
