package org.openmrs.eip;

import java.util.HashMap;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mockito.Mockito;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.config.ReceiverEncryptionProperties;
import org.openmrs.eip.component.config.SenderEncryptionProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;

@EnableAutoConfiguration
@ComponentScan
public class TestCamelDbConfig {
	
	@Bean(name = "testDatabase", initMethod = "start", destroyMethod = "shutdown")
	public TestDatabase getTestDatabase() {
		return new TestDatabase();
	}
	
	@Bean(name = Constants.OPENMRS_DATASOURCE_NAME)
	@ConfigurationProperties(prefix = "spring.openmrs-datasource")
	@DependsOn(Constants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(Constants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	public PropertySource getReceiverPropertySource(ConfigurableEnvironment env) {
		PropertySource customPropSource = new MapPropertySource("testPropSource", new HashMap());
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
	@Bean
	public AppPropertiesBeanPostProcessor appPropertiesBeanPostProcessor() {
		return new AppPropertiesBeanPostProcessor();
	}
	
	@Bean("activeMqConnFactory")
	public ConnectionFactory getConnectionFactory() {
		return Mockito.mock(ConnectionFactory.class);
	}
	
	@Bean("CamelContext")
	public CamelContext getCamelContext() {
		return Mockito.mock(CamelContext.class);
	}
	
	@Bean("producerTemplate")
	public ProducerTemplate getProducerTemplate() {
		return Mockito.mock(ProducerTemplate.class);
	}
	
	@Bean
	public ReceiverEncryptionProperties receiverEncryptionProperties() {
		return Mockito.mock(ReceiverEncryptionProperties.class);
	}
	
	@Bean
	public SenderEncryptionProperties senderEncryptionProperties() {
		return Mockito.mock(SenderEncryptionProperties.class);
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
}
