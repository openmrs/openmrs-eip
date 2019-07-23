package org.openmrs.sync.sender;

import org.apache.camel.CamelContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.sync.core.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.sender.management.init.impl.ManagementDbInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.security.Security;
import java.time.LocalDateTime;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.sender",
                "org.openmrs.sync.core"
        }
)
public class SyncSenderApplication {

    private ManagementDbInit managementDbInit;

    private CamelContext camelContext;

    public SyncSenderApplication(final ManagementDbInit managementDbInit,
                                 final CamelContext camelContext) {
        this.managementDbInit = managementDbInit;
        this.camelContext = camelContext;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SyncSenderApplication.class, args);
    }

    @PostConstruct
    private void initDb() {
        managementDbInit.start();
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
