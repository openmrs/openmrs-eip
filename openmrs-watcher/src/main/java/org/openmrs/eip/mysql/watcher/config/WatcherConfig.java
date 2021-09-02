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
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.openmrs.eip.mysql.watcher.WatcherConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${db-event.destinations}")
	private String dbEventDestinations;
	
	@Bean(WatcherConstants.ERROR_HANDLER_REF)
	@DependsOn(WatcherConstants.PROP_SOURCE_NAME)
	public DeadLetterChannelBuilder getWatcherErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("{{" + PROP_URI_ERROR_HANDLER + "}}");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean(WatcherConstants.SHUTDOWN_HANDLER_REF)
	public DeadLetterChannelBuilder watcherShutdownErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("direct:watcher-shutdown");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
	@Bean("jpaIdempotentRepository")
	public JpaMessageIdRepository getJpaIdempotentRepository(@Qualifier("mngtEntityManager") EntityManagerFactory emf) {
		return new JpaMessageIdRepository(emf, "complexObsProcessor");
	}
	
	@Bean(WatcherConstants.PROP_SOURCE_NAME)
	public PropertySource getCustomPropertySource(ConfigurableEnvironment env) {
		if (StringUtils.split(dbEventDestinations).length > 1) {
			//Remove this after https://issues.openmrs.org/browse/EIP-42 is addressed
			throw new EIPException("Only one value is currently allowed for the property named "
			        + "db-event.destinations, multiple values will be supported in future versions");
		}
		
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
