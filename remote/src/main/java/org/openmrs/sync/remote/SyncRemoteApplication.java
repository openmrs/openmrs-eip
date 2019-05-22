package org.openmrs.sync.remote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"org.openmrs.sync.core.entity"})
@ComponentScan(basePackages = {"org.openmrs.sync.remote.routes", "org.openmrs.sync.remote.config", "org.openmrs.sync.core"})
@EnableJpaRepositories(basePackages = {"org.openmrs.sync.core.repository"})
public class SyncRemoteApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SyncRemoteApplication.class, args);
    }
}
