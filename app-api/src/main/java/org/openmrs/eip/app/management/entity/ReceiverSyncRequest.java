package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Encapsulates info about a request made by the receiver to a remote site to sync an single entity
 * or entities in a specific table
 */
@Entity
@Table(name = "receiver_sync_request")
public class ReceiverSyncRequest extends BaseSyncRequest {
	
	public static final long serialVersionUID = 1;
	
	//TODO Support more statuses e.g. PROCESSED, ERROR, PROCESSED_WITH_CONFLICT
	public enum ReceiverRequestStatus {
		
		NEW,
		
		SENT,
		
		RECEIVED
		
	}
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private ReceiverRequestStatus status;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
	@Column(name = "date_received")
	@Access(AccessType.FIELD)
	private Date dateReceived;
	
	/**
	 * Gets the status
	 *
	 * @return the status
	 */
	public ReceiverRequestStatus getStatus() {
		return status;
	}
	
	/**
	 * Gets the site
	 *
	 * @return the site
	 */
	public SiteInfo getSite() {
		return site;
	}
	
	/**
	 * Sets the site
	 *
	 * @param site the site to set
	 */
	public void setSite(SiteInfo site) {
		this.site = site;
	}
	
	/**
	 * Gets the dateReceived
	 *
	 * @return the dateReceived
	 */
	public Date getDateReceived() {
		return dateReceived;
	}
	
	/**
	 * Marks this request as sent and sets dateSent to current date time
	 */
	public void markAsSent() {
		this.status = ReceiverRequestStatus.SENT;
		updateDateSent();
	}
	
	/**
	 * Marks this request as received, sets dateReceived to current date time and updates the request to
	 * specify if the entity was found or not by the sender in its database
	 * 
	 * @param wasFound specifies if the entity was found by the sender in its database
	 */
	public void markAsReceived(boolean wasFound) {
		this.status = ReceiverRequestStatus.RECEIVED;
		this.dateReceived = new Date();
		if (wasFound) {
			markAsFound();
		}
	}
	
	/**
	 * Creates and returns a {@link SyncRequestModel} object associated to this receiver request
	 * 
	 * @return SyncRequest object
	 */
	public SyncRequestModel buildModel() {
		return new SyncRequestModel(getTableName(), getIdentifier(), getRequestUuid());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{tableName=" + getTableName() + ", identifier=" + getIdentifier() + ", status="
		        + status + ", site=" + getSite() + ", requestUuid=" + getRequestUuid() + "}";
	}
	
}
