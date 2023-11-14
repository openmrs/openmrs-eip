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
	
	/**
	 * Checks if an entity has an item in the sync queue
	 *
	 * @param identifier the entity identifier * @param modelClassname the entity model classname
	 * @return true if the entity has an item in the sync queue otherwise false
	 */
	boolean hasSyncItem(String identifier, String modelClassname);
	
	/**
	 * Checks if an entity has an item in the retry queue
	 * 
	 * @param identifier the entity identifier * @param modelClassname the entity model classname
	 * @return true if the entity has an item in the retry queue otherwise false
	 */
	boolean hasRetryItem(String identifier, String modelClassname);
	
	/**
	 * For a successfully synced message it is moved to the synced queue, in case of error outcome the
	 * message is copied to the error queue before it is moved and in case of a conflict outcome the
	 * item is copied to the conflict queue before it is moved.
	 *
	 * @param message the sync item to post process
	 * @param outcome {@link SyncOutcome}
	 */
	void postProcessSyncItem(SyncMessage message, SyncOutcome outcome);
	
}
