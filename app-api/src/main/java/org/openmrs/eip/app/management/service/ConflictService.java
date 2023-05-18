package org.openmrs.eip.app.management.service;

import java.util.List;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;

/**
 * Contains methods for managing conflicts
 */
public interface ConflictService extends Service {
	
	/**
	 * Gets all conflict queue items where the associated entity in fact has a valid hashes
	 */
	List<ConflictQueueItem> getBadConflicts();
	
}
