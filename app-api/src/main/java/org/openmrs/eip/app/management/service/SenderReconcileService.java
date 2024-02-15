package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.sender.ReconcileSnapshot;

/**
 * Contains sender reconciliation service methods.
 */
public interface SenderReconcileService extends Service {
	
	/**
	 * Creates a snapshot of synced tables in the database.
	 */
	ReconcileSnapshot takeSnapshot();
	
}
