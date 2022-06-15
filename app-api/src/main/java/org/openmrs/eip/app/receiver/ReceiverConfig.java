package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.receiver.ReceiverConstants.ROUTE_ID_INBOUND_DB_SYNC;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.eip.app.SyncConstants;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

public class ReceiverConfig {
	
	@Bean(SyncConstants.CUSTOM_PROP_SOURCE_BEAN_NAME)
	@Profile(SyncProfiles.RECEIVER)
	public PropertySource getReceiverPropertySource(ConfigurableEnvironment env) {
		Map<String, Object> props = new HashMap();
		props.put("message.destination", ROUTE_ID_INBOUND_DB_SYNC);
		PropertySource customPropSource = new MapPropertySource("receiverPropSource", props);
		env.getPropertySources().addLast(customPropSource);
		
		return customPropSource;
	}
	
}
