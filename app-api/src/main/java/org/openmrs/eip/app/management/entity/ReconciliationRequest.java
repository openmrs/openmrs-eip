package org.openmrs.eip.app.management.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates data about a single reconciliation request sent by the receiver to a sender.
 */
public class ReconciliationRequest {
	
	@Getter
	@Setter
	private String identifier;
	
	@Getter
	@Setter
	private int batchSize;
	
}
