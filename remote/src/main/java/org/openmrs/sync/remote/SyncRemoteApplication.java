package org.openmrs.sync.remote;

import org.openmrs.sync.remote.management.init.impl.ManagementDbInit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.remote",
                "org.openmrs.sync.core"
        }
)
public class SyncRemoteApplication {

    private ManagementDbInit managementDbInit;

    public SyncRemoteApplication(final ManagementDbInit managementDbInit) {
        this.managementDbInit = managementDbInit;
    }

    public static void main(final String[] args) {
        SpringApplication.run(SyncRemoteApplication.class, args);
    }

    @PostConstruct
    private void initDb() {
        managementDbInit.start();
    }
}
