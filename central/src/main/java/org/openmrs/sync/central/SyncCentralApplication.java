package org.openmrs.sync.central;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.security.Security;

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

    @PostConstruct
    private void addBCProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
