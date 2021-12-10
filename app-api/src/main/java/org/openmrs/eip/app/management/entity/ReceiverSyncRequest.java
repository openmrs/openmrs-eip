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
	
	public enum ReceiverRequestStatus {
		
		NEW,
		
		SENT,
		
		RECEIVED,
		
		PROCESSED,
		
		ERROR,
		
		CONFLICT
		
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
	 * Sets the status to the specified status and updates date changed
	 * 
	 * @param status the status to set to
	 */
	public void updateStatus(ReceiverRequestStatus status) {
		this.status = status;
		setDateChanged(new Date());
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{modelClassName=" + getModelClassName() + ", identifier=" + getIdentifier()
		        + ", status=" + status + ", site=" + getSite() + "}";
	}
	
}
