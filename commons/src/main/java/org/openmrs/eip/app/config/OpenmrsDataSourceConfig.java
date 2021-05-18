package org.openmrs.eip.app.config;

import javax.sql.DataSource;

import org.openmrs.eip.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenmrsDataSourceConfig {
	
	@Primary
	@Bean(name = Constants.OPENMRS_DATASOURCE_NAME)
	@ConfigurationProperties(prefix = "spring.openmrs-datasource")
	public DataSource dataSource() {
		return DataSourceBuilder.create().build();
	}
	
}
