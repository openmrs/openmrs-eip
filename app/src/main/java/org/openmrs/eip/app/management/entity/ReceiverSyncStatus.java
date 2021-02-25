package org.openmrs.eip.app.management.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "receiver_sync_status")
public class ReceiverSyncStatus extends AbstractEntity {
	
	public static final long serialVersionUID = 1;
	
	//TODO This should be mapped as a @OneToOne association when we build a UI
	@Column(name = "site_info_id", nullable = false, updatable = false, unique = true)
	private Integer siteInfoId;
	
	@Column(name = "last_sync_date", nullable = false)
	private LocalDateTime lastSyncDate;
	
	/**
	 * Gets the siteInfoId
	 *
	 * @return the siteInfoId
	 */
	public Integer getSiteInfoId() {
		return siteInfoId;
	}
	
	/**
	 * Sets the siteInfoId
	 *
	 * @param siteInfoId the siteInfoId to set
	 */
	public void setSiteInfoId(Integer siteInfoId) {
		this.siteInfoId = siteInfoId;
	}
	
	/**
	 * Gets the lastSyncDate
	 *
	 * @return the lastSyncDate
	 */
	public LocalDateTime getLastSyncDate() {
		return lastSyncDate;
	}
	
	/**
	 * Sets the lastSyncDate
	 *
	 * @param lastSyncDate the lastSyncDate to set
	 */
	public void setLastSyncDate(LocalDateTime lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	
}
