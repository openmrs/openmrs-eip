package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.component.model.SyncModel;

/**
 * Contains service methods for the sender
 */
public interface SenderService extends Service {
	
	/**
	 * Prunes the specified sync archive i.e. moves it from the archives to the pruned table
	 *
	 * @param archive the archive to prune
	 */
	void prune(SenderSyncArchive archive);
	
	/**
	 * Moves the specified debezium event to the synced queue
	 *
	 * @param debeziumEvent the DebeziumEvent object to move
	 * @param syncModel the SyncModel object
	 */
	void moveToSyncQueue(DebeziumEvent debeziumEvent, SyncModel syncModel);
	
	/**
	 * Moves the specified retry item to the synced queue
	 *
	 * @param retry the retry item to move
	 * @param syncModel the SyncModel object
	 */
	void moveToSyncQueue(SenderRetryQueueItem retry, SyncModel syncModel);
	
}
