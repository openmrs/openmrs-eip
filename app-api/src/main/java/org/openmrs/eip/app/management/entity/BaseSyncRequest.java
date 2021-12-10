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
	@Column(name = "model_class_name", nullable = false, updatable = false)
	private String modelClassName;
	
	@NotNull
	@Column(nullable = false, updatable = false)
	private String identifier;
	
	@NotNull
	@Column(nullable = false, unique = true, updatable = false, length = 38)
	private String uuid;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	@Access(AccessType.FIELD)
	private Resolution resolution;
	
	@Column(name = "date_changed")
	private Date dateChanged;
	
	/**
	 * Gets the modelClassName
	 *
	 * @return the modelClassName
	 */
	public String getModelClassName() {
		return modelClassName;
	}
	
	/**
	 * Sets the modelClassName
	 *
	 * @param modelClassName the modelClassName to set
	 */
	public void setModelClassName(String modelClassName) {
		this.modelClassName = modelClassName;
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
	 * Gets the uuid
	 *
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * Sets the uuid
	 *
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
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
	 * Gets the dateChanged
	 *
	 * @return the dateChanged
	 */
	public Date getDateChanged() {
		return dateChanged;
	}
	
	/**
	 * Sets the dateChanged
	 *
	 * @param dateChanged the dateChanged to set
	 */
	public void setDateChanged(Date dateChanged) {
		this.dateChanged = dateChanged;
	}
	
	/**
	 * Sets the resolution to the specified resolution and updates date changed
	 * 
	 * @param resolution the resolution to set to
	 */
	public void updateResolution(Resolution resolution) {
		this.resolution = resolution;
		setDateChanged(new Date());
	}
	
}
