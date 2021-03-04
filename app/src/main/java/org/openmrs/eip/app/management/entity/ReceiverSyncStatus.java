package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "receiver_sync_status")
public class ReceiverSyncStatus extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	//TODO This should be mapped as a @OneToOne association when we build a UI
	@Column(name = "site_info_id", nullable = false, updatable = false, unique = true)
	private Long siteInfoId;
	
	@Column(name = "last_sync_date", nullable = false)
	private Date lastSyncDate;
	
	public ReceiverSyncStatus() {
		
	}
	
	public ReceiverSyncStatus(Long siteInfoId, Date lastSyncDate) {
		this.siteInfoId = siteInfoId;
		this.lastSyncDate = lastSyncDate;
	}
	
	/**
	 * Gets the siteInfoId
	 *
	 * @return the siteInfoId
	 */
	public Long getSiteInfoId() {
		return siteInfoId;
	}
	
	/**
	 * Sets the siteInfoId
	 *
	 * @param siteInfoId the siteInfoId to set
	 */
	public void setSiteInfoId(Long siteInfoId) {
		this.siteInfoId = siteInfoId;
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
	
	@Override
	public String toString() {
		return "ReceiverSyncStatus {siteInfoId=" + siteInfoId + ", lastSyncDate=" + lastSyncDate + "}";
	}
}
