package org.openmrs.eip.app.management.service;

import java.util.Set;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.receiver.ConflictResolution;

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
	 * Moves a {@link ConflictQueueItem} to the synced queue
	 *
	 * @param conflict the conflict to move
	 */
	void moveToSyncedQueue(ConflictQueueItem conflict);
	
	/**
	 * Processes a {@link ConflictResolution}
	 *
	 * @param resolution the conflict resolution to process
	 * @throws Exception
	 */
	void resolve(ConflictResolution resolution) throws Exception;
	
	/**
	 * Resolves a conflict as a merge, this method should not be called directly, it's called internally
	 * by the API, the only reason it is publicly declared here is so that it is visible on the spring
	 * bean to be able to run it with a new chained transaction for both the OpenMRS and management
	 * databases, so please instead use {@link #resolve(ConflictResolution)}
	 * 
	 * @param conflict the conflict to resolve
	 * @param propertiesToSync the properties to sync from the new incoming state
	 * @throws Exception
	 */
	void resolveWithMerge(ConflictQueueItem conflict, Set<String> propertiesToSync) throws Exception;
	
}
