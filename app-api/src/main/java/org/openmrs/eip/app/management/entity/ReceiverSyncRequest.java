package org.openmrs.eip.app.management.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;

/**
 * Encapsulates info about a request made by the receiver to a remote site to sync an single entity
 * or entities in a specific table
 */
@Entity
@Table(name = "receiver_sync_request")
@Data
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
	private ReceiverRequestStatus status;
	
	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{modelClassName=" + getModelClassName() + ", identifier=" + getIdentifier()
		        + ", status=" + getStatus() + ", site=" + getSite() + ", resolution=" + getResolution() + "}";
	}
	
}
