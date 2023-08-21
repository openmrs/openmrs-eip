package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for cache evicting processors
 */
public abstract class BaseCacheEvictingProcessor<T extends AbstractEntity> implements PostSyncProcessor<T> {
	
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
	
}
