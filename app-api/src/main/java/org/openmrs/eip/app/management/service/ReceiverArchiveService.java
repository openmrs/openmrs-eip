package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;

/**
 * Contains methods for managing the receiver sync archives
 */
public interface ReceiverArchiveService extends Service {
	
	/**
	 * Prunes the specified sync archive i.e. moves it from the archives queue to the pruned queue
	 * 
	 * @param archive the archive to prune
	 */
	void prune(ReceiverSyncArchive archive);
	
}
