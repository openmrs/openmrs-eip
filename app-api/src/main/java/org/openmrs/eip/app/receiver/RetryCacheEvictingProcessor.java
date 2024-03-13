package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes retry items associated to entities that require eviction from the OpenMRS cache.
 */
@Component("retryCacheEvictProcessor")
@Profile(SyncProfiles.RECEIVER)
public class RetryCacheEvictingProcessor extends BaseCacheEvictingProcessor<ReceiverRetryQueueItem> implements RetryPostSyncProcessor {
	
	public RetryCacheEvictingProcessor(CustomHttpClient client) {
		super(client);
	}
	
}
