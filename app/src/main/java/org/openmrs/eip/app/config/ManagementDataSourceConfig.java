package org.openmrs.eip.app.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "mngtEntityManager", transactionManagerRef = "mngtTransactionManager", basePackages = {
        "org.openmrs.eip.app.management.repository" })
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
	
	@Bean(name = "mngtDataSource")
	@ConfigurationProperties(prefix = "spring.mngt-datasource")
	public DataSource dataSource() throws ClassNotFoundException {
		return DataSourceBuilder.create().build();
	}
	
	@Bean(name = "mngtEntityManager")
	@DependsOn("customPropSource")
	public LocalContainerEntityManagerFactoryBean entityManager(final EntityManagerFactoryBuilder builder,
	                                                            @Qualifier("mngtDataSource") final DataSource dataSource,
	                                                            Environment env) {
		
		Map<String, String> props = new HashMap();
		props.put("hibernate.dialect", hibernateDialect);
		props.put("hibernate.hbm2ddl.auto", "none");
		List<String> entityPackages = new ArrayList();
		entityPackages.add("org.openmrs.eip.app.management.entity");
		if (SyncProfiles.SENDER.equalsIgnoreCase(env.getActiveProfiles()[0])) {
			entityPackages.add("org.apache.camel.processor.idempotent.jpa");
		} else {
			entityPackages.add("org.openmrs.eip.component.management.hash.entity");
		}
		
		return builder.dataSource(dataSource).packages(entityPackages.toArray(new String[entityPackages.size()]))
		        .persistenceUnit("mngt").properties(props).build();
	}
	
	@Bean(name = "mngtTransactionManager")
	public PlatformTransactionManager transactionManager(@Qualifier("mngtEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}
}
