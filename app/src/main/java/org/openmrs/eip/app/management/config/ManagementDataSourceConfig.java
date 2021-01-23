package org.openmrs.eip.app.management.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.sql.Driver;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager", basePackages = {
        "org.openmrs.eip.app.management" })
public class ManagementDataSourceConfig {
	
	@Value("${spring.mngt-datasource.dialect}")
	private String hibernateDialect;
	
	@Value("${spring.mngt-datasource.ddlAuto}")
	private String ddlAuto;
	
	@Value("${spring.mngt-datasource.jdbcUrl}")
	private String url;
	
	@Value("${spring.mngt-datasource.username}")
	private String username;
	
	@Value("${spring.mngt-datasource.password}")
	private String password;
	
	@Value("${spring.mngt-datasource.driverClassName}")
	private String driverClassName;
	
	@Bean(name = "mngtDataSource")
	@ConfigurationProperties(prefix = "spring.mngt-datasource")
	public DataSource dataSource() throws ClassNotFoundException {
		SimpleDriverDataSource sdd = new SimpleDriverDataSource();
		sdd.setDriverClass((Class<Driver>) Class.forName(driverClassName));
		sdd.setUrl(url);
		sdd.setUsername(username);
		sdd.setPassword(password);
		return sdd;
	}
	
	@Bean(name = "mngtEntityManager")
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	                                                            @Qualifier("mngtDataSource") final DataSource dataSource) {
		
		Map<String, String> props = new HashMap<>();
		props.put("hibernate.dialect", hibernateDialect);
		props.put("hibernate.hbm2ddl.auto", ddlAuto);
		
		return builder.dataSource(dataSource)
		        .packages("org.openmrs.eip.app.management.entity", "org.apache.camel.processor.idempotent.jpa")
		        .persistenceUnit("mngt").properties(props).build();
	}
	
	@Bean(name = "mngtTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

    @Bean(name = "liquibase")
    public SpringLiquibase getSpringLiquibaseForMgtDB(@Qualifier("mngtDataSource") DataSource dataSource, Environment env) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setContexts(env.getActiveProfiles()[0]);
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:liquibase-master.xml");
        liquibase.setDatabaseChangeLogTable("liquibasechangelog");
        liquibase.setDatabaseChangeLogLockTable("liquibasechangeloglock");
        liquibase.setShouldRun(false);

        return liquibase;
    }
}
