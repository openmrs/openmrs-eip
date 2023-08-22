package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.component.SyncOperation;

/**
 * Super interface for conflict item post sync processors
 */
public interface ConflictPostSyncProcessor extends PostSyncProcessor<ConflictQueueItem> {
	
	@Override
	default String getModelClassName(ConflictQueueItem item) {
		return item.getModelClassName();
	}
	
	@Override
	default String getIdentifier(ConflictQueueItem item) {
		return item.getIdentifier();
	}
	
	@Override
	default SyncOperation getOperation(ConflictQueueItem item) {
		return item.getOperation();
	}
	
}
