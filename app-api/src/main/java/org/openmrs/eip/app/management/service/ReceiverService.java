package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
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
	 * Adds a sync item to the error and synced queues.
	 * 
	 * @param message the failed sync item
	 * @param exceptionType the exception type name
	 * @param errorMsg the error message
	 */
	void processFailedSyncItem(SyncMessage message, String exceptionType, String errorMsg);
	
	/**
	 * Adds a sync item to the conflict and synced queues.
	 *
	 * @param message the conflicted sync item
	 */
	void processConflictedSyncItem(SyncMessage message);
	
	/**
	 * Moves the specified retry item to the conflict queue
	 *
	 * @param retry the message to move
	 */
	void moveToConflictQueue(ReceiverRetryQueueItem retry);

	/**
	 * Saves a {@link JmsMessage} to the database
	 *
	 * @param jmsMessage the message to save
	 */
	void saveJmsMessage(JmsMessage jmsMessage);
	
	/**
	 * Processes a {@link JmsMessage}
	 *
	 * @param jmsMessage the message to process
	 */
	void processSyncJmsMessage(JmsMessage jmsMessage);
	
}
