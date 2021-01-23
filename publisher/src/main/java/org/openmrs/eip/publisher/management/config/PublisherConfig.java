package org.openmrs.eip.publisher.management.config;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.camel.builder.NoErrorHandlerBuilder;
import org.apache.camel.component.jpa.JpaComponent;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import javax.persistence.EntityManagerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Configuration
@EnableCaching
public class PublisherConfig {
	
	private final static Set<TableToSyncEnum> IGNORE_TABLES;
	
	static {
		IGNORE_TABLES = new HashSet();
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.PROVIDER_ATTRIBUTE);
		IGNORE_TABLES.add(TableToSyncEnum.CONCEPT);
		IGNORE_TABLES.add(TableToSyncEnum.LOCATION);
	}
	
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
	
	@Bean("outBoundErrorHandler")
	public DeadLetterChannelBuilder getOutBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:outbound-error-handler");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("noErrorHandler")
	public NoErrorHandlerBuilder getNoErrorHandler() {
		return new NoErrorHandlerBuilder();
	}
	
	@Bean
	@Profile(SyncProfiles.SENDER)
	public PropertySource getCustomPropertySource(ConfigurableEnvironment env) {
		//Custom PropertySource that we can dynamically populate with generated property values which
		//is not possible via the properties file e.g. to specify names of tables to sync.
		final String dbName = env.getProperty("openmrs.db.name");
		Set<String> tables = new HashSet(TableToSyncEnum.values().length);
		for (TableToSyncEnum tableToSyncEnum : TableToSyncEnum.values()) {
			//TODO Remove the enum values instead including services
			if (IGNORE_TABLES.contains(tableToSyncEnum)) {
				continue;
			}
			
			tables.add(dbName + "." + tableToSyncEnum.name());
		}
		
		Map<String, Object> props = Collections.singletonMap("debezium.tablesToSync", StringUtils.join(tables, ","));
		PropertySource customPropSource = new MapPropertySource("custom", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
