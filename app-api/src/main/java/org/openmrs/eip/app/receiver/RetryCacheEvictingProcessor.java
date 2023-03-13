package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes retry items associated to entities that require eviction from the OpenMRS cache.
 */
@Component("retryCacheEvictingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class RetryCacheEvictingProcessor implements SendToCamelEndpointProcessor<ReceiverRetryQueueItem> {
	
	private ProducerTemplate producerTemplate;
	
	public RetryCacheEvictingProcessor(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	public void process(ReceiverRetryQueueItem item) {
		if (ReceiverUtils.isCached(item.getModelClassName())) {
			send(ReceiverConstants.URI_CLEAR_CACHE, item, producerTemplate);
		}
	}
	
	@Override
	public Object convertBody(ReceiverRetryQueueItem item) {
		return ReceiverUtils.generateEvictionPayload(item.getModelClassName(), item.getIdentifier(), item.getOperation());
	}
	
}
