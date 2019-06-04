package org.openmrs.sync.remote;

import org.apache.camel.CamelContext;
import org.openmrs.sync.core.camel.StringToLocalDateTimeConverter;
import org.openmrs.sync.remote.management.init.impl.ManagementDbInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.remote",
                "org.openmrs.sync.core"
        }
)
public class SyncRemoteApplication {

    private ManagementDbInit managementDbInit;

    private CamelContext camelContext;

    public SyncRemoteApplication(final ManagementDbInit managementDbInit,
                                 final CamelContext camelContext) {
        this.managementDbInit = managementDbInit;
        this.camelContext = camelContext;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SyncRemoteApplication.class, args);
    }

    @PostConstruct
    private void initDb() {
        managementDbInit.start();
    }

    @PostConstruct
    private void addTypeConverter() {
        camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class, new StringToLocalDateTimeConverter());
    }
}
