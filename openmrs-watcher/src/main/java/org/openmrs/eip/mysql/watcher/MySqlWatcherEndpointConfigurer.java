package org.openmrs.eip.mysql.watcher;

import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.support.component.PropertyConfigurerSupport;
import org.apache.camel.util.CaseInsensitiveMap;

public class MySqlWatcherEndpointConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
	
	@Override
	public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
		MySqlWatcherEndpoint endpoint = (MySqlWatcherEndpoint) target;
		
		switch (ignoreCase ? name.toLowerCase() : name) {
			default:
				return false;
		}
	}
	
	@Override
	public Map<String, Object> getAllOptions(Object target) {
		Map<String, Object> options = new CaseInsensitiveMap();
		
		return options;
	}
	
	@Override
	public Object getOptionValue(Object target, String name, boolean ignoreCase) {
		MySqlWatcherEndpoint endpoint = (MySqlWatcherEndpoint) target;
		
		switch (ignoreCase ? name.toLowerCase() : name) {
			default:
				return null;
		}
	}
	
}
