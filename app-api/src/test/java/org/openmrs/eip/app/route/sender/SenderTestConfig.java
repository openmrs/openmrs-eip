package org.openmrs.eip.app.route.sender;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.springframework.context.annotation.Bean;

public class SenderTestConfig {
	
	@Bean("outBoundErrorHandler")
	public DeadLetterChannelBuilder getOutBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:test-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
