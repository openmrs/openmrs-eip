package org.openmrs.eip;

import javax.jms.ConnectionFactory;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.mockito.Mockito;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.config.ReceiverEncryptionProperties;
import org.openmrs.eip.component.config.SenderEncryptionProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@EnableAutoConfiguration
@ComponentScan
public class TestConfig {
	
	@Bean(Constants.OPENMRS_DATASOURCE_NAME)
	public DataSource getDataSource() {
		return DataSourceBuilder.create().url(BaseDbDrivenTest.mysqlContainer.getJdbcUrl() + "?useSSL=false")
		        .username(BaseDbDrivenTest.mysqlContainer.getUsername())
		        .password(BaseDbDrivenTest.mysqlContainer.getPassword()).build();
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
