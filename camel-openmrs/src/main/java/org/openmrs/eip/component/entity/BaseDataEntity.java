package org.openmrs.eip.component.entity;

import static java.util.Collections.singleton;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.utils.DateUtils;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;

/**
 * OpenMRS data model distinguishes between data and metadata, please refer to the javadocs in
 * OpenMRS on BaseOpenmrsData and BaseOpenmrsMetadata classes, this is the superclass for classes in
 * this project that represent data entities.
 */
@MappedSuperclass
public abstract class BaseDataEntity extends BaseCreatableEntity {
	
	@NotNull
	@Column(name = "voided")
	private boolean voided;
	
	@ManyToOne
	@JoinColumn(name = "voided_by")
	private UserLight voidedBy;
	
	@Column(name = "date_voided")
	private LocalDateTime dateVoided;
	
	@Column(name = "void_reason")
	private String voidReason;
	
	@Override
	public boolean wasModifiedAfter(final BaseEntity entity) {
		BaseDataEntity other = (BaseDataEntity) entity;
		return DateUtils.containsLatestDate(singleton(getDateVoided()), singleton(other.getDateVoided()));
	}
	
	/**
	 * Gets the voided
	 *
	 * @return the voided
	 */
	public boolean isVoided() {
		return voided;
	}
	
	/**
	 * Sets the voided
	 *
	 * @param voided the voided to set
	 */
	public void setVoided(boolean voided) {
		this.voided = voided;
	}
	
	/**
	 * Gets the voidedBy
	 *
	 * @return the voidedBy
	 */
	public UserLight getVoidedBy() {
		return voidedBy;
	}
	
	/**
	 * Sets the voidedBy
	 *
	 * @param voidedBy the voidedBy to set
	 */
	public void setVoidedBy(UserLight voidedBy) {
		this.voidedBy = voidedBy;
	}
	
	/**
	 * Gets the dateVoided
	 *
	 * @return the dateVoided
	 */
	public LocalDateTime getDateVoided() {
		return dateVoided;
	}
	
	/**
	 * Sets the dateVoided
	 *
	 * @param dateVoided the dateVoided to set
	 */
	public void setDateVoided(LocalDateTime dateVoided) {
		this.dateVoided = dateVoided;
	}
	
	/**
	 * Gets the voidReason
	 *
	 * @return the voidReason
	 */
	public String getVoidReason() {
		return voidReason;
	}
	
	/**
	 * Sets the voidReason
	 *
	 * @param voidReason the voidReason to set
	 */
	public void setVoidReason(String voidReason) {
		this.voidReason = voidReason;
	}
	
}
