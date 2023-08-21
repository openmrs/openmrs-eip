package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for search index updating processors
 */
public abstract class BaseSearchIndexUpdatingProcessor<T extends AbstractEntity> implements PostSyncProcessor<T> {
	
	private ProducerTemplate producerTemplate;
	
	public BaseSearchIndexUpdatingProcessor(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	public void process(T item) {
		if (ReceiverUtils.isIndexed(getModelClassName(item))) {
			send(ReceiverConstants.URI_UPDATE_SEARCH_INDEX, item, producerTemplate);
		}
	}
	
	@Override
	public Object convertBody(T item) {
		return ReceiverUtils.generateSearchIndexUpdatePayload(getModelClassName(item), getIdentifier(item),
		    getOperation(item));
	}
	
}
