package org.openmrs.eip.app.management.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
		NEW, SENT, RECEIVED, SUCCESS, ERROR, CONFLICT
	}
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "site_id", nullable = false, updatable = false)
	private SiteInfo site;
	
	private ReceiverRequestStatus status;
	
}
