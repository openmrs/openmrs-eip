package org.cicr.sync.remote;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = {"org.cicr.sync.core.entity"})
public class CamelRemoteApplication {

    public static void main(final String[] args) {
        SpringApplication.run(CamelRemoteApplication.class, args);
    }
}
