package org.openmrs.eip.app.config;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.receiver.ReceiverConstants;
import org.openmrs.eip.app.receiver.ReceiverMessageListener;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Session;

@Configuration
@Profile(SyncProfiles.RECEIVER)
public class ReceiverJmsConfig {
	
	@Value("${" + ReceiverConstants.PROP_SYNC_QUEUE + "}")
	private String queueName;
	
	@Bean
	public DefaultMessageListenerContainer getListenerContainer(ConnectionFactory cf, ReceiverMessageListener listener,
	                                                            @Qualifier(Constants.MGT_TX_MGR) PlatformTransactionManager transactionManager) {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(cf);
		container.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
		container.setDestinationName(queueName);
		container.setMessageListener(listener);
		//The config below ensures that message acknowledgement in the broker is done inside the same transaction as the
		//one we use to save the message to the DB, that way when the message is successfully saved to DB the message 
		//then acknowledged is sent to the broker otherwise never.
		container.setSessionTransacted(true);
		container.setTransactionManager(transactionManager);
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
