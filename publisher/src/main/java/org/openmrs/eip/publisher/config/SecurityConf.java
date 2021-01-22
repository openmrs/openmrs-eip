package org.openmrs.eip.publisher.config;

import org.openmrs.eip.component.config.ReceiverEncryptionProperties;
import org.openmrs.eip.component.config.SenderEncryptionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConf {
	
	@Bean
	@ConfigurationProperties(prefix = "pgp.sender")
	public SenderEncryptionProperties senderProperties() {
		return new SenderEncryptionProperties();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "pgp.receiver")
	public ReceiverEncryptionProperties receiverProperties() {
		return new ReceiverEncryptionProperties();
	}
}
