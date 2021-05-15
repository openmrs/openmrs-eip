package org.openmrs.eip.app.management.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.hibernate.dialect.MySQL5Dialect;
import org.openmrs.eip.Constants;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
@EnableCaching
public class AppConfig {
	
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
		props.put("spring.jpa.properties.hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		props.put("spring.jpa.properties.hibernate.dialect", MySQL5Dialect.class.getName());
		props.put("spring.jpa.properties.hibernate.hbm2ddl.auto", "none");
		PropertySource customPropSource = new MapPropertySource("commonPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
