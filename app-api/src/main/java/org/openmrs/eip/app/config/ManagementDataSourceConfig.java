package org.openmrs.eip.app.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager", basePackages = {
        "org.openmrs.eip.app.management.repository" })
public class ManagementDataSourceConfig {
	
	@Value("${spring.mngt-datasource.dialect}")
	private String hibernateDialect;
	
	@Bean(name = SyncConstants.MGT_DATASOURCE_NAME)
	@ConfigurationProperties(prefix = "spring.mngt-datasource")
	public DataSource dataSource() {
		HikariDataSource ds = ((HikariDataSource) DataSourceBuilder.create().build());
		ds.setPoolName(SyncConstants.DEFAULT_MGT_POOL_NAME);
		ds.setMaximumPoolSize(SyncConstants.DEFAULT_CONN_POOL_SIZE);
		return ds;
	}
	
	@Bean(name = SyncConstants.MGT_ENTITY_MGR)
	@DependsOn(SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	                                                            @Qualifier("mngtDataSource") final DataSource dataSource,
	                                                            Environment env) {
		
		Map<String, String> props = new HashMap();
		props.put("hibernate.dialect", hibernateDialect);
		props.put("hibernate.hbm2ddl.auto", "none");
		List<String> entityPackages = new ArrayList();
		entityPackages.add("org.openmrs.eip.app.management.entity");
		if (ArrayUtils.contains(env.getActiveProfiles(), SyncProfiles.SENDER)) {
			entityPackages.add("org.apache.camel.processor.idempotent.jpa");
		} else {
			entityPackages.add("org.openmrs.eip.component.management.hash.entity");
		}
		
		return builder.dataSource(dataSource).packages(entityPackages.toArray(new String[entityPackages.size()]))
		        .persistenceUnit("mngt").properties(props).build();
	}
	
	@Bean(name = SyncConstants.MGT_TX_MGR)
	public PlatformTransactionManager transactionManager(@Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
	
	@Bean(name = "liquibase")
	public SpringLiquibase getSpringLiquibaseForMgtDB(@Qualifier("mngtDataSource") DataSource dataSource, Environment env) {
		SpringLiquibase liquibase = new SpringLiquibase();
		liquibase.setContexts(StringUtils.join(env.getActiveProfiles(), ","));
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog("classpath:liquibase-master.xml");
		liquibase.setDatabaseChangeLogTable("LIQUIBASECHANGELOG");
		liquibase.setDatabaseChangeLogLockTable("LIQUIBASECHANGELOGLOCK");
		liquibase.setShouldRun(true);
		
		return liquibase;
	}
	
}
