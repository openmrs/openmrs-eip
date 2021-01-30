package org.openmrs.eip.app.publisher;

import org.apache.camel.CamelContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class Application {
	
	private CamelContext camelContext;
	
	public Application(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
