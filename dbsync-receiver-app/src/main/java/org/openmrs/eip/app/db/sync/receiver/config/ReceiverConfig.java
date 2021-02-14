package org.openmrs.eip.app.db.sync.receiver.config;

import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.openmrs.eip.app.management.config.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.jms.connection.CachingConnectionFactory;

@Configuration
public class ReceiverConfig {
	
	@Bean("receiverErrorHandler")
	public DeadLetterChannelBuilder getReceiverErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dbsync-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean(Constants.PROP_SOURCE_BEAN_NAME)
	public PropertySource getReceiverPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = new HashMap();
		props.put(Constants.PROP_PACKAGES_TO_SCAN, "org.openmrs.eip.app.db.sync.receiver.management.entity");
		props.put("message.destination", "db-sync");
		PropertySource customPropSource = new MapPropertySource("receiverPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
	@Bean("activeMqConnFactory")
	public ConnectionFactory getConnectionFactory(Environment env) {
		CachingConnectionFactory cf = new CachingConnectionFactory(new ActiveMQConnectionFactory());
		cf.setClientId(env.getProperty("activemq.clientId"));
		
		return cf;
	}
	
}
