package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for cache evicting processors
 */
public abstract class BaseCacheEvictingProcessor<T extends AbstractEntity> implements PostSyncProcessor<T> {
	
	private CustomHttpClient client;
	
	public BaseCacheEvictingProcessor(CustomHttpClient client) {
		this.client = client;
	}
	
	public void process(T item) {
		if (ReceiverUtils.isCached(getModelClassName(item))) {
			sendRequest(CACHE_RESOURCE, item, client);
		}
	}
	
	@Override
	public Object convertBody(T item) {
		return ReceiverUtils.generateEvictionPayload(getModelClassName(item), getIdentifier(item), getOperation(item));
	}
	
}
