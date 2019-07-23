package org.openmrs.sync.receiver.config;

import org.openmrs.sync.core.config.ReceiverEncryptionProperties;
import org.openmrs.sync.core.config.SenderEncryptionProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConf {

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
