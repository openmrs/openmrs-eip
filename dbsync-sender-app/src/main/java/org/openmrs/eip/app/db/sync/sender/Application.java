package org.openmrs.eip.app.db.sync.sender;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class Application {
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
