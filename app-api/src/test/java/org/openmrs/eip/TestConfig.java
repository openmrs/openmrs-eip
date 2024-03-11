package org.openmrs.eip;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.mockito.Mockito;
import org.openmrs.eip.app.config.AppConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.jms.core.JmsTemplate;

@EnableAutoConfiguration(exclude = { JmsAutoConfiguration.class })
@ComponentScan
@Import({ AppConfig.class })
public class TestConfig {
	
	@Bean
	public DeadLetterChannelBuilder shutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder(TestConstants.URI_ERROR_HANDLER);
		builder.useOriginalMessage();
		return builder;
	}
	
	@Bean
	public DeadLetterChannelBuilder deadLetterChannelBuilder() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder(TestConstants.URI_ERROR_HANDLER);
		builder.useOriginalMessage();
		return builder;
	}
	
	@Bean
	public JmsTemplate jmsTemplate() {
		return Mockito.mock(JmsTemplate.class);
	}
	
}
