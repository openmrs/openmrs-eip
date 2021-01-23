package org.openmrs.eip.app;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.NoErrorHandlerBuilder;
import org.apache.camel.processor.idempotent.jpa.JpaMessageIdRepository;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class SyncApplication {
	
	private CamelContext camelContext;
	
	public SyncApplication(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(SyncApplication.class, args);
	}
	
	@Bean("outBoundErrorHandler")
	public DeadLetterChannelBuilder getOutBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:outbound-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("inBoundErrorHandler")
	public DeadLetterChannelBuilder getInBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:inbound-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("noErrorHandler")
	public NoErrorHandlerBuilder getNoErrorHandler() {
		return new NoErrorHandlerBuilder();
	}
	
	@Bean("jpaIdempotentRepository")
	@Profile(SyncProfiles.SENDER)
	public JpaMessageIdRepository getJpaIdempotentRepository(@Qualifier("mngtEntityManager") EntityManagerFactory emf) {
		return new JpaMessageIdRepository(emf, "complexObsProcessor");
	}
	
}
