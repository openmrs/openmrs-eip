package org.openmrs.eip.app.management.service;

import java.time.LocalDateTime;

import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
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
	 * Moves a debezium event to the synced queue
	 *
	 * @param debeziumEvent the DebeziumEvent object to move
	 * @param syncModel the SyncModel object
	 */
	void moveEventToSyncQueue(DebeziumEvent debeziumEvent, SyncModel syncModel);
	
	/**
	 * Moves a retry item to the synced queue
	 *
	 * @param retry the retry item to move
	 * @param syncModel the SyncModel object
	 */
	void moveRetryToSyncQueue(SenderRetryQueueItem retry, SyncModel syncModel);
	
	/**
	 * Moves a debezium event to the retry queue.
	 *
	 * @param debeziumEvent the DebeziumEvent object to move
	 * @param exceptionType the fully qualified Java class name of the thrown exception
	 * @param errorMessage the error message
	 */
	void moveToRetryQueue(DebeziumEvent debeziumEvent, String exceptionType, String errorMessage);
	
	/**
	 * Moves a {@link SenderSyncMessage} to the archive queue
	 *
	 * @param message the message to archive
	 * @param dateReceivedByReceiver the date the sync message was received by the receiver
	 */
	void archiveSyncMessage(SenderSyncMessage message, LocalDateTime dateReceivedByReceiver);
	
}
