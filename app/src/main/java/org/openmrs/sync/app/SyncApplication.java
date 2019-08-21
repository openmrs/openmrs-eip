package org.openmrs.sync.app;

import org.apache.camel.CamelContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.sync.component.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.app.management.init.impl.ManagementDbInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.lang.Nullable;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.time.LocalDateTime;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.app",
                "org.openmrs.sync.component",
                "org.openmrs.sync.map"
        }
)
public class SyncApplication {

    private ManagementDbInit managementDbInit;

    private CamelContext camelContext;

    public SyncApplication(@Nullable final ManagementDbInit managementDbInit,
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
}
