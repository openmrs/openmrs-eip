package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for search index updating processors
 */
public abstract class BaseSearchIndexUpdatingProcessor<T extends AbstractEntity> implements PostSyncProcessor<T> {
	
	private CustomHttpClient client;
	
	public BaseSearchIndexUpdatingProcessor(CustomHttpClient client) {
		this.client = client;
	}
	
	public void process(T item) {
		if (ReceiverUtils.isIndexed(getModelClassName(item))) {
			sendRequest(INDEX_RESOURCE, item, client);
		}
	}
	
	@Override
	public Object convertBody(T item) {
		return ReceiverUtils.generateSearchIndexUpdatePayload(getModelClassName(item), getIdentifier(item),
		    getOperation(item));
	}
	
}
