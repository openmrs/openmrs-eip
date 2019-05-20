package org.cicr.sync.central;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"org.cicr.sync.core.entity"})
public class CamelCentralApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CamelCentralApplication.class, args);
    }
}
