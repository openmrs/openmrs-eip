package org.openmrs.eip.app.management.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.apache.camel.component.jpa.DefaultTransactionStrategy;
import org.apache.camel.component.jpa.JpaComponent;
import org.hibernate.cfg.AvailableSettings;
import org.openmrs.eip.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager")
public class ManagementDataSourceConfig {
	
	@Value("${spring.mngt-datasource.dialect}")
	private String hibernateDialect;
	
	@Value("${spring.mngt-datasource.jdbcUrl}")
	private String url;
	
	@Value("${spring.mngt-datasource.username}")
	private String username;
	
	@Value("${spring.mngt-datasource.password}")
	private String password;
	
	@Value("${spring.mngt-datasource.driverClassName}")
	private String driverClassName;
	
	@Bean(name = Constants.MGT_DATASOURCE_NAME)
	@ConfigurationProperties(prefix = "spring.mngt-datasource")
	public DataSource dataSource() throws ClassNotFoundException {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "mngtEntityManager")
	@DependsOn(Constants.COMMON_PROP_SOURCE_BEAN_NAME)
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	        @Qualifier("mngtDataSource") final DataSource dataSource, ConfigurableEnvironment env) {
		
		Map<String, String> props = new HashMap<>();
		props.put(AvailableSettings.DIALECT, hibernateDialect);
		props.put(AvailableSettings.HBM2DDL_AUTO, "none");
		
		return builder.dataSource(dataSource).packages(env.getProperty(Constants.PROP_PACKAGES_TO_SCAN, String[].class))
		        .persistenceUnit("mngt").properties(props).build();
	}
	
	@Bean(name = "mngtTransactionManager")
	public PlatformTransactionManager transactionManager(
	        @Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(value = "jpa")
	public JpaComponent jpa(@Qualifier(value = "mngtEntityManager") EntityManagerFactory entityManagerFactory,
	        @Qualifier(value = "mngtTransactionManager") PlatformTransactionManager txMgr) {
		JpaComponent component = new JpaComponent();
		component.setEntityManagerFactory(entityManagerFactory);
		component.setTransactionStrategy(new DefaultTransactionStrategy(component.getCamelContext(), entityManagerFactory));
		return component;
	}
	
	@Bean(name = Constants.LIQUIBASE_BEAN_NAME)
	public SpringLiquibase getSpringLiquibaseForMgtDB(@Qualifier("mngtDataSource") DataSource dataSource) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:liquibase-watcher.xml");
		liquibase.setDatabaseChangeLogTable("LIQUIBASECHANGELOG");
		liquibase.setDatabaseChangeLogLockTable("LIQUIBASECHANGELOGLOCK");
		liquibase.setShouldRun(true);
		
		return liquibase;
	}
	
}
