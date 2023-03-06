package org.openmrs.eip.app;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Superclass for all BaseCamelQueueProcessor instances that send each queue item to a camel
 * endpoint URI for processing
 * 
 * @param <T> item type
 */
public abstract class BaseFromCamelToCamelEndpointProcessor<T extends AbstractEntity> extends BaseFromCamelProcessor<T> implements SendToCamelEndpointProcessor<T> {
	
	private String endpointUri;
	
	private ProducerTemplate producerTemplate;
	
	public BaseFromCamelToCamelEndpointProcessor(String endpointUri, ProducerTemplate producerTemplate,
	    ThreadPoolExecutor executor) {
		super(executor);
		this.endpointUri = endpointUri;
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public void processItem(T item) {
		send(endpointUri, item, producerTemplate);
	}
	
}
