package org.openmrs.eip.app;

import java.security.Security;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;
import javax.jms.ConnectionFactory;

import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.NoErrorHandlerBuilder;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.eip.app.config.JpaCamelConf;
import org.openmrs.eip.app.config.ManagementDataSourceConfig;
import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class, JpaCamelConf.class })
public class SyncApplication {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncApplication.class);
	
	private static final long REDELIVERY_DELAY = 300000;
	
	private CamelContext camelContext;
	
	@Value("${max.reconnect.delay:1800000}")
	private int maxReconnectDelay;
	
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
	
	@Bean("activeMqConnFactory")
	public ConnectionFactory getConnectionFactory(Environment env) {
		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
		String url = "tcp://" + env.getProperty("spring.artemis.host") + ":" + env.getProperty("spring.artemis.port");
		String failoverUrl = "eip-failover:(" + url
		        + ")?initialReconnectDelay=60000&reconnectDelayExponent=5&maxReconnectDelay=" + maxReconnectDelay
		        + "&maxReconnectAttempts=-1&warnAfterReconnectAttempts=2";
		cf.setBrokerURL(failoverUrl);
		cf.setUserName(env.getProperty("spring.artemis.user"));
		cf.setPassword(env.getProperty("spring.artemis.password"));
		final String clientId = env.getProperty("activemq.clientId");
		if (StringUtils.isNotBlank(clientId)) {
			cf.setClientID(clientId);
		}
		
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(RedeliveryPolicy.NO_MAXIMUM_REDELIVERIES);
		redeliveryPolicy.setInitialRedeliveryDelay(REDELIVERY_DELAY);
		redeliveryPolicy.setRedeliveryDelay(REDELIVERY_DELAY);
		cf.setRedeliveryPolicy(redeliveryPolicy);
		
		return new CachingConnectionFactory(cf);
	}
	
	@Bean
	public DeadLetterChannelBuilder shutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:shutdown-route");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
