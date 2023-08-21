package org.openmrs.eip.app.receiver;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.component.SyncOperation;

/**
 * Super interface for retry item post sync processors
 */
public interface RetryPostSyncProcessor extends PostSyncProcessor<ReceiverRetryQueueItem> {
	
	@Override
	default String getModelClassName(ReceiverRetryQueueItem item) {
		return item.getModelClassName();
	}
	
	@Override
	default String getIdentifier(ReceiverRetryQueueItem item) {
		return item.getIdentifier();
	}
	
	@Override
	default SyncOperation getOperation(ReceiverRetryQueueItem item) {
		return item.getOperation();
	}
	
}
