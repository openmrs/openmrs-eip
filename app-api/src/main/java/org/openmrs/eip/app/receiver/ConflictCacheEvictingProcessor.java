package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes conflict items associated to entities that require eviction from the OpenMRS cache.
 */
@Component("conflictCacheEvictProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ConflictCacheEvictingProcessor extends BaseCacheEvictingProcessor<ConflictQueueItem> implements ConflictPostSyncProcessor {
	
	public ConflictCacheEvictingProcessor(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
}
