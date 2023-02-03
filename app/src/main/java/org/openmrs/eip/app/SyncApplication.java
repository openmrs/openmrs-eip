package org.openmrs.eip.app;

import java.security.Security;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.NoErrorHandlerBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.eip.app.config.ActiveMqConfig;
import org.openmrs.eip.app.config.JpaCamelConf;
import org.openmrs.eip.app.config.ManagementDataSourceConfig;
import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class, JpaCamelConf.class, ActiveMqConfig.class })
public class SyncApplication {
	
	private CamelContext camelContext;
	
	public SyncApplication(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(SyncApplication.class, args);
	}
	
	@PostConstruct
	private void addTypeConverter() {
		camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class,
		    new StringToLocalDateTimeConverter());
	}
	
	@PostConstruct
	private void addBCProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/**
	 * Bean to handle messages in error and re-route them to another route
	 *
	 * @return deadLetterChannelBuilder
	 */
	@Bean
	public DeadLetterChannelBuilder deadLetterChannelBuilder() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dlc");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("noErrorHandler")
	public NoErrorHandlerBuilder getNoErrorHandler() {
		return new NoErrorHandlerBuilder();
	}
	
	@Bean
	public DeadLetterChannelBuilder shutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:shutdown-route");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
