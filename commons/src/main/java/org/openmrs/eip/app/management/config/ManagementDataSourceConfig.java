package org.openmrs.eip.app.management.config;

import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.camel.component.jpa.JpaComponent;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.dialect.MySQL5Dialect;
import org.openmrs.eip.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager")
public class ManagementDataSourceConfig {
	
	@Value("${spring.mngt-datasource.dialect:#{null}}")
	private String hibernateDialect;
	
	@Value("${spring.mngt-datasource.jdbcUrl:#{null}}")
	private String url;
	
	@Value("${spring.mngt-datasource.username:#{null}}")
	private String username;
	
	@Value("${spring.mngt-datasource.password:#{null}}")
	private String password;
	
	@Value("${spring.mngt-datasource.driverClassName:#{null}}")
	private String driverClassName;
	
	@Bean(name = Constants.MGT_DATASOURCE_NAME)
	@ConfigurationProperties(prefix = "spring.mngt-datasource")
	public DataSource dataSource(@Qualifier(Constants.OPENMRS_DATASOURCE_NAME) DataSource openmrsDatasource)
	    throws ClassNotFoundException {
		
		//If no mgt DB is specified, use OpenMRS DB
		if (url == null) {
			return openmrsDatasource;
		}
		
		SimpleDriverDataSource sdd = new SimpleDriverDataSource();
		sdd.setDriverClass((Class<Driver>) Class.forName(driverClassName));
		sdd.setUrl(url);
		sdd.setUsername(username);
		sdd.setPassword(password);
		return sdd;
	}
	
	@Bean(name = "mngtEntityManager")
	@DependsOn(Constants.COMMON_PROP_SOURCE_BEAN_NAME)
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	                                                            @Qualifier("mngtDataSource") final DataSource dataSource,
	                                                            ConfigurableEnvironment env) {
		
		Map<String, String> props = new HashMap();
		//Set to mysql dialect since if we have defaulted to using OpenMRS DB for mgt
		//TODO support postgres
		props.put(AvailableSettings.DIALECT, url != null ? hibernateDialect : MySQL5Dialect.class.getName());
		props.put(AvailableSettings.HBM2DDL_AUTO, "none");
		
		return builder.dataSource(dataSource).packages(env.getProperty(Constants.PROP_PACKAGES_TO_SCAN, String[].class))
		        .persistenceUnit("mngt").properties(props).build();
	}
	
	@Bean(name = "mngtTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(value = "jpa")
	public JpaComponent jpa(@Qualifier(value = "mngtEntityManager") EntityManagerFactory entityManagerFactory,
	                        @Qualifier(value = "mngtTransactionManager") PlatformTransactionManager txMgr) {
		
		JpaComponent comp = new JpaComponent();
		comp.setEntityManagerFactory(entityManagerFactory);
		comp.setTransactionManager(txMgr);
		
		return comp;
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
