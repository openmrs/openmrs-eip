package org.openmrs.eip;

import org.openmrs.eip.app.config.JpaCamelConf;
import org.openmrs.eip.app.config.ManagementDataSourceConfig;
import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class, JpaCamelConf.class })
public class TestDBConfig {
	
	@Bean
	public AppPropertiesBeanPostProcessor appPropertiesBeanPostProcessor() {
		return new AppPropertiesBeanPostProcessor();
	}
	
}
