package org.openmrs.eip.app.receiver.task;

import org.openmrs.eip.app.receiver.processor.ReceiverRetryProcessor;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@Profile(SyncProfiles.RECEIVER)
public class ReceiverTaskConfig {
	
	@Bean
	private ReceiverRetryTask receiverRetryTask(ReceiverRetryProcessor processor) {
		return new ReceiverRetryTask(processor);
	}
	
}
