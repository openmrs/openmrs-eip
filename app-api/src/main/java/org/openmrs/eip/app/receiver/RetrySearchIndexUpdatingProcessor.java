package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes retry items associated to entities that require updating the OpenMRS search index.
 */
@Component("retrySearchIndexUpdateProcessor")
@Profile(SyncProfiles.RECEIVER)
public class RetrySearchIndexUpdatingProcessor extends BaseSearchIndexUpdatingProcessor<ReceiverRetryQueueItem> implements RetryPostSyncProcessor {
	
	public RetrySearchIndexUpdatingProcessor(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
}
