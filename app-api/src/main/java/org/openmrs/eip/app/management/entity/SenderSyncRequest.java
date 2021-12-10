package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Encapsulates info about a request received by a remote site from a receiver to sync an single
 * entity or entities in a specific table
 */
@Entity
@Table(name = "sender_sync_request")
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
	@Access(AccessType.FIELD)
	private SenderRequestStatus status;
	
	/**
	 * Gets the status
	 *
	 * @return the status
	 */
	public SenderRequestStatus getStatus() {
		return status;
	}
	
	/**
	 * Sets the status to the specified status and updates date changed
	 *
	 * @param status the status to set to
	 */
	public void updateStatus(SenderRequestStatus status) {
		this.status = status;
		setDateChanged(new Date());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{modelClassName=" + getModelClassName() + ", identifier=" + getIdentifier()
		        + "status=" + status + "}";
	}
}
