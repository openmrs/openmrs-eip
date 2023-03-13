package org.openmrs.eip.app.config;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.springframework.context.annotation.Bean;

public class CamelConfig {
	
	/**
	 * Bean to handle messages in error and re-route them to another route
	 *
	 * @return deadLetterChannelBuilder
	 */
	@Bean
	public DeadLetterChannelBuilder deadLetterChannelBuilder() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dlc");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean
	public DeadLetterChannelBuilder shutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:shutdown-route");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
