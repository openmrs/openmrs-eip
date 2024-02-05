package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;

/**
 * Contains reconciliation service methods.
 */
public interface ReconcileService extends Service {
	
	/**
	 * Processes a {@link JmsMessage}
	 *
	 * @param jmsMessage the message to process
	 */
	void processSyncJmsMessage(JmsMessage jmsMessage);
	
	/**
	 * Updates the processed count, if the uuids were found, they are marked as found otherwise a sync
	 * request is created for the associated entity.
	 *
     * 
	 * @param message the message to update
	 * @param found specifies whether the uuids were found or not
	 * @param uuids the uuids
	 */
	void updateReconciliationMessage(ReconciliationMessage message, boolean found, String... uuids);
	
}
