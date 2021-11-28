package org.openmrs.eip;

import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class })
public class TestDBConfig {
	
	@Bean
	public AppPropertiesBeanPostProcessor appPropertiesBeanPostProcessor() {
		return new AppPropertiesBeanPostProcessor();
	}
	
}
