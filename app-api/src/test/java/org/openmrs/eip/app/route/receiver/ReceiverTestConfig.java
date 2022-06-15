package org.openmrs.eip.app.route.receiver;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.openmrs.eip.app.receiver.ReceiverConfig;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Import(ReceiverConfig.class)
public class ReceiverTestConfig {
	
	@Bean("inBoundErrorHandler")
	@Profile(SyncProfiles.RECEIVER)
	public DeadLetterChannelBuilder getInBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:test-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
