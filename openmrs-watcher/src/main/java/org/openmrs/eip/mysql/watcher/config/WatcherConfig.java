package org.openmrs.eip.mysql.watcher.config;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_URI_ERROR_HANDLER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.processor.idempotent.jpa.JpaMessageIdRepository;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.Constants;
import org.openmrs.eip.Utils;
import org.openmrs.eip.mysql.watcher.WatcherConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
@ComponentScan("org.openmrs.eip.mysql.watcher")
public class WatcherConfig {
	
	@Bean(WatcherConstants.ERROR_HANDLER_REF)
	@DependsOn(WatcherConstants.PROP_SOURCE_NAME)
	public DeadLetterChannelBuilder getWatcherErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("{{" + PROP_URI_ERROR_HANDLER + "}}");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("jpaIdempotentRepository")
	public JpaMessageIdRepository getJpaIdempotentRepository(@Qualifier("mngtEntityManager") EntityManagerFactory emf) {
		return new JpaMessageIdRepository(emf, "complexObsProcessor");
	}
	
	@Bean(Constants.PROP_SOURCE_BEAN_NAME)
	public PropertySource getWatcherPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = new HashMap();
		props.put(Constants.PROP_PACKAGES_TO_SCAN,
		    new String[] { "org.openmrs.eip.mysql.watcher.management.entity", "org.apache.camel.processor.idempotent.jpa" });
		PropertySource customPropSource = new MapPropertySource(Constants.PROP_SOURCE_BEAN_NAME, props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
	@Bean(WatcherConstants.PROP_SOURCE_NAME)
	public PropertySource getCustomPropertySource(ConfigurableEnvironment env) {
		//Custom PropertySource that we can dynamically populate with generated property values which
		//is not possible via the properties file e.g. to specify names of tables to sync.
		final String dbName = env.getProperty("openmrs.db.name");
		Set<String> tables = new HashSet(Utils.getWatchedTables().size());
		for (String table : Utils.getWatchedTables()) {
			tables.add(dbName + "." + table);
		}
		
		Map<String, Object> props = new HashMap();
		props.put("debezium.tablesToSync", StringUtils.join(tables, ","));
		props.put(WatcherConstants.PROP_URI_EVENT_PROCESSOR, WatcherConstants.URI_EVENT_PROCESSOR);
		props.put(PROP_URI_ERROR_HANDLER, WatcherConstants.URI_ERROR_HANDLER);
		PropertySource customPropSource = new MapPropertySource(WatcherConstants.PROP_SOURCE_NAME, props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
