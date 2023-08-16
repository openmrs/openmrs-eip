package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;

/**
 * Contains methods for managing conflicts
 */
public interface ConflictService extends Service {
	
	/**
	 * Moves the specified conflict item to the retry queue and returns the created retry item
	 *
	 * @param conflict the conflict to move
	 * @param reason the reason for moving the item
	 * @return the created retry item
	 */
	ReceiverRetryQueueItem moveToRetryQueue(ConflictQueueItem conflict, String reason);
	
	/**
	 * Moves the specified conflict item to the archive queue and returns the created archive item
	 *
	 * @param conflict the conflict to move
	 * @return the created archive item
	 */
	ReceiverSyncArchive moveToArchiveQueue(ConflictQueueItem conflict);
	
	/**
	 * Resolves the specified conflict with the winning state being that in the receiver database, i.e.
	 * the incoming state is ignored and the item is just moved to the archives.
	 * 
	 * @param conflict the conflict to resolve
	 */
	void resolveWithDatabaseState(ConflictQueueItem conflict);
	
	/**
	 * Resolves the specified conflict with the winning state being incoming one from the remote site,
	 * i.e. do regular sync of incoming state to overwrite the database state.
	 * 
	 * @param conflict the conflict to resolve
	 */
	void resolveWithIncomingState(ConflictQueueItem conflict);
	
	/**
	 * Resolves the specified conflict with the winning state being the specified merged state
	 * 
	 * @param conflict the conflict to resolve
	 * @param mergedState the effective merged state
	 */
	void resolveWithMerge(ConflictQueueItem conflict, Object mergedState);
	
}
