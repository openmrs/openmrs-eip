package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;

/**
 * Contains methods for managing receiver items
 */
public interface ReceiverService extends Service {
	
	/**
	 * Prunes the specified sync archive i.e. moves it from the archives queue to the pruned queue
	 * 
	 * @param archive the archive to prune
	 */
	void prune(ReceiverSyncArchive archive);
	
}
