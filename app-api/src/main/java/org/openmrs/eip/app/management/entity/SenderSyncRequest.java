package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;

/**
 * Encapsulates info about a request received by a remote site from a receiver to sync an single
 * entity or entities in a specific table
 */
@Entity
@Table(name = "sender_sync_request")
@Data
public class SenderSyncRequest extends BaseSyncRequest {
	
	public static final long serialVersionUID = 1;
	
	public enum SenderRequestStatus {
		NEW, SENT
	}
	
	private SenderRequestStatus status;
	
}
