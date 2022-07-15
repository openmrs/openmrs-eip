package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME;
import static org.openmrs.eip.component.Constants.PROP_URI_ERROR_HANDLER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.camel.builder.DeadLetterChannelBuilder;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

@Configuration
@Profile(SyncProfiles.SENDER)
public class SenderConfig {
	
	@Bean(CUSTOM_PROP_SOURCE_BEAN_NAME)
	public PropertySource getSenderPropertySource(ConfigurableEnvironment env) {
		//Custom PropertySource that we can dynamically populate with generated property values which
		//is not possible via the properties file e.g. to specify names of tables to sync.
		final String dbName = env.getProperty("openmrs.db.name");
		Set<String> tableNames = AppUtils.getTablesToSync();
		Set<String> tables = new HashSet(tableNames.size());
		for (String table : tableNames) {
			tables.add(dbName + "." + table);
		}
		
		Map<String, Object> props = new HashMap();
		props.put("debezium.tablesToSync", StringUtils.join(tables, ","));
		props.put(SenderConstants.PROP_ACTIVEMQ_IN_ENDPOINT, SenderConstants.ACTIVEMQ_IN_ENDPOINT);
		props.put("spring.jpa.properties.hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		props.put(PROP_URI_ERROR_HANDLER, "direct:outbound-error-handler");
		PropertySource customPropSource = new MapPropertySource("senderPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
	@Bean(SenderConstants.ERROR_HANDLER_REF)
	@DependsOn(CUSTOM_PROP_SOURCE_BEAN_NAME)
	public DeadLetterChannelBuilder getOutBoundErrorHandler() {
		DeadLetterChannelBuilder builder = new DeadLetterChannelBuilder("{{" + PROP_URI_ERROR_HANDLER + "}}");
		builder.setUseOriginalMessage(true);
		return builder;
	}
	
}
