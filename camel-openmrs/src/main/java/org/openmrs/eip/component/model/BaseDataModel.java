package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

public abstract class BaseDataModel extends BaseModel {
	
	private boolean voided;
	
	private String voidedByUuid;
	
	private LocalDateTime dateVoided;
	
	private String voidReason;
	
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
	 * Gets the voidedByUuid
	 *
	 * @return the voidedByUuid
	 */
	public String getVoidedByUuid() {
		return voidedByUuid;
	}
	
	/**
	 * Sets the voidedByUuid
	 *
	 * @param voidedByUuid the voidedByUuid to set
	 */
	public void setVoidedByUuid(String voidedByUuid) {
		this.voidedByUuid = voidedByUuid;
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
