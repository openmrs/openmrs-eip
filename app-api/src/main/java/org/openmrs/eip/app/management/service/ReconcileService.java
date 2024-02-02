package org.openmrs.eip.app.management.service;

import org.openmrs.eip.app.management.entity.receiver.JmsMessage;

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
	
}
