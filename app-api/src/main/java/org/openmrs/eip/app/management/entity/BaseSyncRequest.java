package org.openmrs.eip.app.management.entity;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

/**
 * Base class for sync request classes for the Sender and Receiver
 */
@MappedSuperclass
public abstract class BaseSyncRequest extends AbstractEntity {
	
	public enum Resolution {
		PENDING, FOUND, NOT_FOUND
	}
	
	@NotNull
	@Column(name = "table_name", nullable = false, updatable = false)
	private String tableName;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(nullable = false, unique = true, updatable = false, length = 38)
	private String requestUuid;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private Resolution resolution;
	
	@Column(name = "resolution_date")
	@Access(AccessType.FIELD)
	private Date resolutionDate;
	
	@Column(name = "date_sent")
	@Access(AccessType.FIELD)
	private Date dateSent;
	
	/**
	 * Gets the tableName
	 *
	 * @return the tableName
	 */
	public String getTableName() {
		return tableName;
	}
	
	/**
	 * Sets the tableName
	 *
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	/**
	 * Gets the identifier
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}
	
	/**
	 * Sets the identifier
	 *
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	/**
	 * Gets the requestUuid
	 *
	 * @return the requestUuid
	 */
	public String getRequestUuid() {
		return requestUuid;
	}
	
	/**
	 * Sets the requestUuid
	 *
	 * @param requestUuid the requestUuid to set
	 */
	public void setRequestUuid(String requestUuid) {
		this.requestUuid = requestUuid;
	}
	
	/**
	 * Gets the resolution
	 *
	 * @return the resolution
	 */
	public Resolution getResolution() {
		return resolution;
	}
	
	/**
	 * Gets the dateSent
	 *
	 * @return the dateSent
	 */
	public Date getDateSent() {
		return dateSent;
	}
	
	/**
	 * Gets the dateResolved
	 *
	 * @return the dateResolved
	 */
	public Date getResolutionDate() {
		return resolutionDate;
	}
	
	/**
	 * Sets the resolution to FOUND and updates resolutionDate
	 */
	public void markAsFound() {
		updateResolution(Resolution.FOUND);
	}
	
	/**
	 * Sets the resolution to NOT_FOUND and updates resolutionDate
	 */
	public void markAsNotFound() {
		updateResolution(Resolution.NOT_FOUND);
	}
	
	/**
	 * Sets the dateSent to current date and time
	 */
	protected void updateDateSent() {
		this.dateSent = new Date();
	}
	
	/**
	 * Sets the resolution to the specified resolution and updates resolutionDate
	 *
	 * @param resolution the resolution to set to
	 */
	private void updateResolution(Resolution resolution) {
		this.resolution = resolution;
		this.resolutionDate = new Date();
	}
	
}
