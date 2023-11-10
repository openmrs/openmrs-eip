package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;

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
	
}
