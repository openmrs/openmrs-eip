package org.openmrs.eip.app.management.service;

import java.util.List;

import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;

/**
 * Contains sender reconciliation service methods.
 */
public interface SenderReconcileService extends Service {
	
	/**
	 * Creates a snapshot of synced tables in the database.
	 */
	List<SenderTableReconciliation> takeSnapshot();
	
	/**
	 * Saves the snapshot data
	 *
	 * @param reconciliation the sender reconciliation
	 * @param tableReconciliations the list to save
	 */
	void saveSnapshot(SenderReconciliation reconciliation, List<SenderTableReconciliation> tableReconciliations);
	
}
