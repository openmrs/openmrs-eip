package org.openmrs.eip.mysql.watcher;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.openmrs.eip.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TestOpenmrsDataSourceConfig {
	
	@Bean(name = "openmrsTestEntityManager")
	@DependsOn(Constants.COMMON_PROP_SOURCE_BEAN_NAME)
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	        @Qualifier(Constants.OPENMRS_DATASOURCE_NAME) final DataSource dataSource) {
		
		return builder.dataSource(dataSource).packages("org.openmrs.eip").persistenceUnit("openmrs").build();
	}
	
	@Primary
	@Bean(name = "openmrsTestTxManager")
	public PlatformTransactionManager transactionManager(
	        @Qualifier("openmrsTestEntityManager") final EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
