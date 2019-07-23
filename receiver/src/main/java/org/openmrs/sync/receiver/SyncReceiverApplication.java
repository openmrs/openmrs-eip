package org.openmrs.sync.receiver;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import java.security.Security;

@SpringBootApplication
@ComponentScan(
        basePackages = {
                "org.openmrs.sync.receiver",
                "org.openmrs.sync.core"
        }
)
public class SyncReceiverApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SyncReceiverApplication.class, args);
    }

    @PostConstruct
    private void addBCProvider() {
        Security.addProvider(new BouncyCastleProvider());
    }
}
