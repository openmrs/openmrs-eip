package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.SyncOperation;

/**
 * Base class for cache evicting processors
 */
public abstract class BaseCacheEvictingProcessor<T extends AbstractEntity> implements SendToCamelEndpointProcessor<T> {
	
	private ProducerTemplate producerTemplate;
	
	public BaseCacheEvictingProcessor(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	public void process(T item) {
		if (ReceiverUtils.isCached(getModelClassName(item))) {
			send(ReceiverConstants.URI_CLEAR_CACHE, item, producerTemplate);
		}
	}
	
	@Override
	public Object convertBody(T item) {
		return ReceiverUtils.generateEvictionPayload(getModelClassName(item), getIdentifier(item), getOperation(item));
	}
	
	/**
	 * Gets the model classname for the entity
	 * 
	 * @param item the associated sync entity
	 * @return model classname
	 */
	public abstract String getModelClassName(T item);
	
	/**
	 * Gets the unique identifier for the entity
	 *
	 * @param item the associated sync entity
	 * @return unique identifier
	 */
	public abstract String getIdentifier(T item);
	
	/**
	 * Gets the {@link SyncOperation} for the sync event
	 *
	 * @param item the associated sync entity
	 * @return operation
	 */
	public abstract SyncOperation getOperation(T item);
	
}
