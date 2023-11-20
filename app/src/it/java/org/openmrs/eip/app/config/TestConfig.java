package org.openmrs.eip.app.config;

import org.apache.camel.ProducerTemplate;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.config.ReceiverEncryptionProperties;
import org.openmrs.eip.component.config.SenderEncryptionProperties;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAutoConfiguration
@EnableJpaRepositories(entityManagerFactoryRef = "entityManager", basePackages = { "org.openmrs.eip.component.repository" })
@EntityScan("org.openmrs.eip.component.entity")
@ComponentScan({ "org.openmrs.eip.component.service", "org.openmrs.eip.component.mapper",
        "org.openmrs.eip.component.camel" })
public class TestConfig {
	
	@Value("${spring.datasource.dialect}")
	private String hibernateDialect;
	
	@Value("${spring.datasource.ddlAuto}")
	private String ddlAuto;
	
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "pgp.receiver")
	public ReceiverEncryptionProperties receiverEncryptionProperties() {
		return new ReceiverEncryptionProperties();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "pgp.sender")
	public SenderEncryptionProperties senderProperties() {
		return new SenderEncryptionProperties();
	}
	
	@Bean
	public SyncContext getSyncContext() {
		SyncContext syncContext = Mockito.mock(SyncContext.class);
		ApplicationContext mockAppContext = Mockito.mock(ApplicationContext.class);
		Whitebox.setInternalState(SyncContext.class, ApplicationContext.class, mockAppContext);
		ProducerTemplate mockProducerTemplate = Mockito.mock(ProducerTemplate.class);
		Mockito.when(mockAppContext.getBean(ProducerTemplate.class)).thenReturn(mockProducerTemplate);
		return syncContext;
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	                                                            final DataSource dataSource) {
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.dialect", hibernateDialect);
		props.put("hibernate.hbm2ddl.auto", ddlAuto);
		
		return builder.dataSource(dataSource).packages("org.openmrs.eip.component.entity").persistenceUnit("openmrs")
		        .properties(props).build();
	}
}
