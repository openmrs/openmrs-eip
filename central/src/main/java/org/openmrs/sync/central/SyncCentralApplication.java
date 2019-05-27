package org.openmrs.sync.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.central",
                "org.openmrs.sync.core"
        }
)
public class SyncCentralApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SyncCentralApplication.class, args);
    }
}
