package org.openmrs.eip.example;

import org.openmrs.eip.app.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(AppConfig.class)
public class ExampleApplication {
	
	private static final Logger logger = LoggerFactory.getLogger(ExampleApplication.class);
	
	public static void main(final String[] args) {
		logger.info("Starting example application...");
		
		SpringApplication.run(ExampleApplication.class, args);
	}
	
}
