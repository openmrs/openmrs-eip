package org.openmrs.eip.app.publisher;

import java.util.Collections;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.openmrs.eip.app.management.config.Constants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
public class Application {
	
	private CamelContext camelContext;
	
	public Application(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean(Constants.PROP_SOURCE_BEAN_NAME)
	public PropertySource getPublisherPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = Collections.singletonMap(Constants.PROP_PACKAGES_TO_SCAN,
		    new String[] { "org.openmrs.eip.mysql.watcher.management.entity", "org.apache.camel.processor.idempotent.jpa" });
		PropertySource customPropSource = new MapPropertySource("publisherPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
