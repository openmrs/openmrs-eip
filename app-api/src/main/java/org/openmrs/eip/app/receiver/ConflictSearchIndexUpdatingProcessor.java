package org.openmrs.eip.app.receiver;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes conflict items associated to entities that require updating the OpenMRS search index.
 */
@Component("conflictSearchIndexUpdateProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ConflictSearchIndexUpdatingProcessor extends BaseSearchIndexUpdatingProcessor<ConflictQueueItem> implements ConflictPostSyncProcessor {
	
	public ConflictSearchIndexUpdatingProcessor(ProducerTemplate producerTemplate) {
		super(producerTemplate);
	}
	
}
