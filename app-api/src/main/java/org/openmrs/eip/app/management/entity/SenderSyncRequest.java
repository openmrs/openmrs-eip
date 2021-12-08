package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
		
		NEW,
		
		PROCESSED,
		
		ERROR
		
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private SenderRequestStatus status;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{modelClassName=" + getModelClassName() + ", identifier=" + getIdentifier()
		        + "status=" + status + ", resolution=" + getResolution() + "}";
	}
}
