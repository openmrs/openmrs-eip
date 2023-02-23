package org.openmrs.eip.app;

import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Superclass for all processors that send each queue item to a camel endpoint URI for processing
 * 
 * @param <T> item type
 */
public abstract class BaseToCamelEndpointProcessor<T extends AbstractEntity> extends BaseCamelQueueProcessor<T> {
	
	@Override
	public void processItem(T item) {
		producerTemplate.sendBody(getEndpointUri(), item);
	}
	
	/**
	 * Gets the camel endpoint URI to send to a single item for processing
	 *
	 * @return the camel URI
	 */
	public abstract String getEndpointUri();
	
}
