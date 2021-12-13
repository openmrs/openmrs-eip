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
	
	//TODO Support more statuses e.g. PROCESSING, ERROR
	public enum SenderRequestStatus {
		
		NEW,
		
		SENT
		
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
	 * Marks this request as sent and sets dateSent to current date time
	 */
	public void markAsSent() {
		this.status = SenderRequestStatus.SENT;
		updateDateSent();
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{tableName=" + getTableName() + ", identifier=" + getIdentifier() + "status="
		        + status + ", requestUuid=" + getRequestUuid() + ", requestUuid=" + status + "}";
	}
	
}
