package org.openmrs.eip.app.management.service;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;

/**
 * Contains receiver reconciliation service methods.
 */
public interface ReceiverReconcileService extends Service {
	
	/**
	 * Processes a {@link JmsMessage}
	 *
	 * @param jmsMessage the message to process
	 */
	void processJmsMessage(JmsMessage jmsMessage);
	
	/**
	 * Updates the status of a ReconciliationMessage and TableReconciliation, If the uuids were found,
	 * they are marked as found otherwise a sync request is created for the associated entity.
	 *
	 * @param message the message to update
	 * @param found specifies whether the uuids were found or not
	 * @param uuids the uuids
	 */
	void updateReconciliationMessage(ReconciliationMessage message, boolean found, List<String> uuids);
	
}
