package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.SendToCamelEndpointProcessor;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes retry items associated to entities that require updating the OpenMRS search index.
 */
@Component("retrySearchIndexUpdatingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class RetrySearchIndexUpdatingProcessor implements SendToCamelEndpointProcessor<ReceiverRetryQueueItem> {
	
	private ProducerTemplate producerTemplate;
	
	public RetrySearchIndexUpdatingProcessor(ProducerTemplate producerTemplate) {
		this.producerTemplate = producerTemplate;
	}
	
	public void process(ReceiverRetryQueueItem item) {
		if (ReceiverUtils.isIndexed(item.getModelClassName())) {
			send(ReceiverConstants.URI_UPDATE_SEARCH_INDEX, item, producerTemplate);
		}
	}
	
	@Override
	public Object convertBody(ReceiverRetryQueueItem item) {
		return ReceiverUtils.generateSearchIndexUpdatePayload(item.getModelClassName(), item.getIdentifier(),
		    item.getOperation());
	}
	
}
