package org.openmrs.eip.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.openmrs.eip.Constants;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@EnableCaching
@ComponentScan("org.openmrs.eip")
@org.springframework.context.annotation.PropertySource("classpath:application-common.properties")
public class CommonConfig {
	
	/**
	 * Bean to handle messages in error and re-route them to another route
	 *
	 * @return deadLetterChannelBuilder
	 */
	@Bean
	public DeadLetterChannelBuilder deadLetterChannelBuilder() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:dlc");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean(Constants.COMMON_PROP_SOURCE_BEAN_NAME)
	public PropertySource getCommonPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = new HashMap();
		props.put(Constants.PROP_PACKAGES_TO_SCAN,
		    new String[] { "org.openmrs.eip.mysql.watcher.management.entity", "org.apache.camel.processor.idempotent.jpa" });
		
		PropertySource customPropSource = new MapPropertySource("commonPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
