package org.openmrs.eip.app.management.entity;

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
	
	//TODO Support more statuses e.g. PROCESSING, PROCESSED, ERROR, SENT
	public enum SenderRequestStatus {
		
		NEW,
		
		SENT
		
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private SenderRequestStatus status = SenderRequestStatus.NEW;
	
	/**
	 * Gets the status
	 *
	 * @return the status
	 */
	public SenderRequestStatus getStatus() {
		return status;
	}
	
	/**
	 * Marks this request as sent, sets dateSent to current date time and updates the request to specify
	 * if the entity was found or not by the sender in its database
	 * 
	 * @param wasFound specifies if the entity was found by the sender in its database
	 */
	public void markAsSent(boolean wasFound) {
		this.status = SenderRequestStatus.SENT;
		updateDateSent();
		if (wasFound) {
			markAsFound();
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{tableName=" + getTableName() + ", identifier=" + getIdentifier() + ", status="
		        + status + ", requestUuid=" + getRequestUuid() + "}";
	}
	
}
