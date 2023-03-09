package org.openmrs.eip;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.NoErrorHandlerBuilder;
import org.openmrs.eip.app.config.AppConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@EnableAutoConfiguration
@ComponentScan
@Import({ AppConfig.class })
public class TestConfig {
	
	@Bean
	public DeadLetterChannelBuilder shutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder(TestConstants.URI_ERROR_HANDLER);
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("noErrorHandler")
	public NoErrorHandlerBuilder getNoErrorHandler() {
		return new NoErrorHandlerBuilder();
	}
	
}
