package org.openmrs.eip.publisher;

import org.apache.camel.CamelContext;
import org.apache.camel.spi.GeneratedPropertyConfigurer;
import org.apache.camel.spi.PropertyConfigurerGetter;
import org.apache.camel.support.component.PropertyConfigurerSupport;
import org.apache.camel.util.CaseInsensitiveMap;

import java.util.Map;

public class PublisherEndpointConfigurer extends PropertyConfigurerSupport implements GeneratedPropertyConfigurer, PropertyConfigurerGetter {
	
	private static final String ERROR_HANDLER_PROP = "errorHandlerRef";
	
	private static final String ERROR_HANDLER_PROP_LOWERCASE = "errorhandlerref";
	
	@Override
	public boolean configure(CamelContext camelContext, Object target, String name, Object value, boolean ignoreCase) {
		PublisherEndpoint endpoint = (PublisherEndpoint) target;
		
		switch (ignoreCase ? name.toLowerCase() : name) {
			case ERROR_HANDLER_PROP_LOWERCASE:
			case ERROR_HANDLER_PROP:
				endpoint.setErrorHandlerRef(property(camelContext, String.class, value));
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public Map<String, Object> getAllOptions(Object target) {
		Map<String, Object> options = new CaseInsensitiveMap();
		options.put(ERROR_HANDLER_PROP, String.class);
		
		return options;
	}
	
	@Override
	public Object getOptionValue(Object target, String name, boolean ignoreCase) {
		PublisherEndpoint endpoint = (PublisherEndpoint) target;
		
		switch (ignoreCase ? name.toLowerCase() : name) {
			case ERROR_HANDLER_PROP_LOWERCASE:
			case ERROR_HANDLER_PROP:
				return endpoint.getErrorHandlerRef();
			default:
				return null;
		}
	}
	
}
