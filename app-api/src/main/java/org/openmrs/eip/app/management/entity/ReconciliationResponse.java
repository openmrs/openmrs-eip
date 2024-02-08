package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * Encapsulates data about a single reconciliation message sent by the sender to the receiver.
 */
public class ReconciliationResponse {
	
	@Getter
	@Setter
	private String identifier;
	
	@Getter
	@Setter
	private String tableName;
	
	@Getter
	@Setter
	private Long rowCount;
	
	@Getter
	@Setter
	private LocalDateTime remoteStartDate;
	
	@Getter
	@Setter
	private int batchSize;
	
	@Getter
	@Setter
	private boolean lastTableBatch;
	
	@Getter
	@Setter
	private String data;
	
}
