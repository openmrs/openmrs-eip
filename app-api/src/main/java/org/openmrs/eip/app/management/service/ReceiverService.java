package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;

/**
 * Contains methods for managing receiver items
 */
public interface ReceiverService extends Service {
	
	/**
	 * Moves the specified {@link SyncMessage} to the synced queue
	 *
	 * @param message the message to move
	 * @param outcome {@link SyncOutcome}
	 */
	void moveToSyncedQueue(SyncMessage message, SyncOutcome outcome);
	
	/**
	 * Moves the specified {@link SyncedMessage} to the archive queue
	 *
	 * @param message the message to archive
	 */
	void archiveSyncedMessage(SyncedMessage message);
	
	/**
	 * Moves the specified {@link ReceiverRetryQueueItem} to the archive queue
	 *
	 * @param retry the retry to archive
	 */
	void archiveRetry(ReceiverRetryQueueItem retry);
	
	/**
	 * Prunes the specified sync archive i.e. moves it from the archive queue to the pruned queue
	 * 
	 * @param archive the archive to prune
	 */
	void prune(ReceiverSyncArchive archive);
	
	/**
	 * Updates the entity hash to match the current state in the database
	 * 
	 * @param modelClassname the model classname of the entity
	 * @param identifier the entity unique identifier
	 */
	void updateHash(String modelClassname, String identifier);
	
}
