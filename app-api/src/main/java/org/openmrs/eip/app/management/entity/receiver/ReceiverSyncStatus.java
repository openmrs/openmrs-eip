package org.openmrs.eip.app.management.entity.receiver;

import java.util.Date;

import org.openmrs.eip.app.management.entity.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "receiver_sync_status")
public class ReceiverSyncStatus extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	@OneToOne(optional = false)
	@JoinColumn(name = "site_info_id", nullable = false, updatable = false, unique = true)
	private SiteInfo siteInfo;
	
	@Column(name = "last_sync_date", nullable = false)
	private Date lastSyncDate;
	
	public ReceiverSyncStatus() {
	}
	
	public ReceiverSyncStatus(SiteInfo siteInfo, Date lastSyncDate) {
		this.siteInfo = siteInfo;
		this.lastSyncDate = lastSyncDate;
	}
	
	public SiteInfo getSiteInfo() {
		return siteInfo;
	}
	
	public void setSiteInfo(SiteInfo siteInfo) {
		this.siteInfo = siteInfo;
	}
	
	/**
	 * Gets the lastSyncDate
	 *
	 * @return the lastSyncDate
	 */
	public Date getLastSyncDate() {
		return lastSyncDate;
	}
	
	/**
	 * Sets the lastSyncDate
	 *
	 * @param lastSyncDate the lastSyncDate to set
	 */
	public void setLastSyncDate(Date lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
	public String toString() {
		return "ReceiverSyncStatus {site=" + this.siteInfo + ", lastSyncDate=" + lastSyncDate + "}";
	}
}
