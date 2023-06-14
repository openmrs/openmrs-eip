package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
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
	 * Prunes the specified sync archive i.e. moves it from the archives queue to the pruned queue
	 * 
	 * @param archive the archive to prune
	 */
	void prune(ReceiverSyncArchive archive);
	
}
