package org.openmrs.eip.component.config;

import java.security.Security;

import javax.annotation.PostConstruct;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
	
	@PostConstruct
	private void addBCProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
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
