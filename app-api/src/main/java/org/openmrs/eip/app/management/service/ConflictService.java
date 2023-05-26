package org.openmrs.eip.app.management.service;

import java.util.List;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;

/**
 * Contains methods for managing conflicts
 */
public interface ConflictService extends Service {
	
	/**
	 * Gets all conflict queue items where the associated entity in fact has a valid hashes
	 */
	List<ConflictQueueItem> getBadConflicts();
	
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
	
}
