package org.openmrs.eip.app.management.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import jakarta.persistence.EntityManagerFactory;

import org.apache.camel.component.jpa.DefaultTransactionStrategy;
import org.apache.camel.component.jpa.JpaComponent;
import org.hibernate.cfg.AvailableSettings;
import org.openmrs.eip.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LockException;
import liquibase.integration.spring.SpringLiquibase;
import liquibase.lockservice.LockServiceFactory;

@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager")
public class ManagementDataSourceConfig {
	
	private static final Logger log = LoggerFactory.getLogger(ManagementDataSourceConfig.class);
	
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
		releaseLiquibaseLock(dataSource);
		
		return liquibase;
	}
	
	public void releaseLiquibaseLock(DataSource dataSource) {
		try (Connection connection = dataSource.getConnection()) {
			Database database = DatabaseFactory.getInstance()
			        .findCorrectDatabaseImplementation(new JdbcConnection(connection));
			LockServiceFactory.getInstance().getLockService(database).forceReleaseLock();
		}
		catch (DatabaseException | SQLException | LockException e) {
			log.error("Error occurred while liquibase forceReleaseLock {}", e.getMessage(), e);
		}
	}
}
