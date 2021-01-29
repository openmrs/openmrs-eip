package org.openmrs.eip.publisher;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.spi.annotations.Component;
import org.apache.camel.support.DefaultComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("openmrseip-publisher")
public class PublisherComponent extends DefaultComponent {
	
	private static final Logger logger = LoggerFactory.getLogger(PublisherComponent.class);
	
	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) {
		logger.info("Creating publisher endpoint with parameters: " + parameters);
		return new PublisherEndpoint(uri, this);
	}
	
}
