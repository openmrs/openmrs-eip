package org.openmrs.eip.app.db.sync.receiver;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class Application {
	
	private CamelContext camelContext;
	
	public Application(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean("inBoundErrorHandler")
	public DeadLetterChannelBuilder getInBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dbsync-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
