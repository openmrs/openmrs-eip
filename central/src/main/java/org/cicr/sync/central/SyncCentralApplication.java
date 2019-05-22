package org.cicr.sync.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"org.cicr.sync.core.entity"})
@ComponentScan(basePackages = {"org.cicr.sync.central.routes", "org.cicr.sync.central.config", "org.cicr.sync.core"})
@EnableJpaRepositories(basePackages = {"org.cicr.sync.core.repository"})
public class SyncCentralApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SyncCentralApplication.class, args);
    }
}
