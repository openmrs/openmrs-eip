package org.openmrs.eip.app.publisher;

import java.util.Collections;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.openmrs.eip.app.management.config.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class Application {
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
}
