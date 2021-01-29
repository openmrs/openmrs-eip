package org.openmrs.eip.publisher;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "1.0.0", scheme = "openmrseip-publisher", title = "OpenMRS EIP Publisher", syntax = "openmrseip-publisher:listener", label = "openmrs,eip,publisher", producerOnly = true)
public class PublisherEndpoint extends DefaultEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(PublisherEndpoint.class);
	
	public PublisherEndpoint(String endpointUri, Component component) {
		this.setEndpointUri(endpointUri);
		this.setComponent(component);
	}
	
	@Override
	public Producer createProducer() {
		logger.info("Creating publisher producer");
		return new PublisherProducer(this);
	}
	
	@Override
	public Consumer createConsumer(Processor processor) {
		throw new UnsupportedOperationException();
	}
	
}
