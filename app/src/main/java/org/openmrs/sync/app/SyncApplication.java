package org.openmrs.sync.app;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.sync.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.app.management.init.impl.ManagementDbInitImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.time.LocalDateTime;

@SpringBootApplication(scanBasePackages = {
        "org.openmrs.sync.app",
        "org.openmrs.sync.component",
        "org.openmrs.utils.odoo"
})
public class SyncApplication {

    private ManagementDbInitImpl managementDbInit;

    private CamelContext camelContext;

    public SyncApplication(@Nullable final ManagementDbInitImpl managementDbInit,
                           final CamelContext camelContext) {
        this.managementDbInit = managementDbInit;
        this.camelContext = camelContext;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SyncApplication.class, args);
    }

    @PostConstruct
    private void initDb() {
        if (managementDbInit != null) {
            managementDbInit.start();
        }
    }

    @PostConstruct
    private void addTypeConverter() {
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
    }

    @PostConstruct
    private void addBCProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * Bean to handle messages in error and re-route them to another route
     * @return deadLetterChannelBuilder
     */
    @Bean
    public DeadLetterChannelBuilder deadLetterChannelBuilder() {
        DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dlc");
        builder.setUseOriginalMessage(true);
        return builder;
    }
}
