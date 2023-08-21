package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes retry items associated to entities that require eviction from the OpenMRS cache.
 */
@Component("retryCacheEvictingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class RetryCacheEvictingProcessor extends BaseCacheEvictingProcessor<ReceiverRetryQueueItem> {
	
	public RetryCacheEvictingProcessor(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
	@Override
	public String getModelClassName(ReceiverRetryQueueItem item) {
		return item.getModelClassName();
	}
	
	@Override
	public String getIdentifier(ReceiverRetryQueueItem item) {
		return item.getIdentifier();
	}
	
	@Override
	public SyncOperation getOperation(ReceiverRetryQueueItem item) {
		return item.getOperation();
	}
	
}
