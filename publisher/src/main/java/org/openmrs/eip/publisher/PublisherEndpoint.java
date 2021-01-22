package org.openmrs.eip.publisher;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UriEndpoint(firstVersion = "1.0.0", scheme = "openmrseip-publisher", title = "OpenMRS EIP Publisher", syntax = "openmrseip-publisher:listener", label = "openmrs,eip,publisher", producerOnly = true)
public class PublisherEndpoint extends DefaultEndpoint {
	
	private static final Logger logger = LoggerFactory.getLogger(PublisherEndpoint.class);
	
	@UriPath
	@Metadata(required = true)
	private String listener;
	
	@UriParam(label = "errorHandlerRef")
	private String errorHandlerRef;
	
	public PublisherEndpoint(String endpointUri, Component component, String listener) {
		this.setEndpointUri(endpointUri);
		this.setComponent(component);
		this.listener = listener;
	}
	
	/**
	 * Gets the listener
	 *
	 * @return the listener
	 */
	public String getListener() {
		return listener;
	}
	
	/**
	 * Gets the errorHandlerRef
	 *
	 * @return the errorHandlerRef
	 */
	public String getErrorHandlerRef() {
		return errorHandlerRef;
	}
	
	/**
	 * Sets the errorHandlerRef
	 *
	 * @param errorHandlerRef the errorHandlerRef to set
	 */
	public void setErrorHandlerRef(String errorHandlerRef) {
		this.errorHandlerRef = errorHandlerRef;
	}
	
	@Override
	public Producer createProducer() {
		logger.info("Creating publisher producer with listener");
		return new PublisherProducer(this);
	}
	
	@Override
	public Consumer createConsumer(Processor processor) {
		throw new UnsupportedOperationException();
	}
	
}
