package org.openmrs.eip;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.app.config.ManagementDataSourceConfig;
import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Import({ ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class })
public class TestDBConfig {
	
	@Bean
	public AppPropertiesBeanPostProcessor appPropertiesBeanPostProcessor() {
		return new AppPropertiesBeanPostProcessor();
	}
	
	@Bean(SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	public PropertySource getReceiverPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = new HashMap();
		PropertySource customPropSource = new MapPropertySource("testPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
