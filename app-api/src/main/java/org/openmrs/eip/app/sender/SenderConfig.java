package org.openmrs.eip.app.sender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

import org.apache.camel.processor.idempotent.jpa.JpaMessageIdRepository;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

public class SenderConfig {
	
	@Bean(SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	@Profile(SyncProfiles.SENDER)
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
		props.put("spring.jpa.properties.hibernate.physical_naming_strategy", SpringPhysicalNamingStrategy.class.getName());
		PropertySource customPropSource = new MapPropertySource("senderPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
	@Bean("jpaIdempotentRepository")
	@Profile(SyncProfiles.SENDER)
	public JpaMessageIdRepository getJpaIdempotentRepository(@Qualifier("mngtEntityManager") EntityManagerFactory emf) {
		return new JpaMessageIdRepository(emf, "complexObsProcessor");
	}
	
}
