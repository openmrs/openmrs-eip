package org.openmrs.eip.app.db.sync.receiver.config;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReceiverConfig {
	
	@Bean("inBoundErrorHandler")
	public DeadLetterChannelBuilder getReceiverErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dbsync-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
