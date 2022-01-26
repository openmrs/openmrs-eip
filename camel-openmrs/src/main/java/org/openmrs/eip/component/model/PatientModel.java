package org.openmrs.eip.component.model;

import java.time.LocalDateTime;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PatientModel extends PersonModel {
	
	private String allergyStatus;
	
	private String patientCreatorUuid;
	
	private LocalDateTime patientDateCreated;
	
	private String patientChangedByUuid;
	
	private LocalDateTime patientDateChanged;
	
	private boolean patientVoided;
	
	private String patientVoidedByUuid;
	
	private LocalDateTime patientDateVoided;
	
	private String patientVoidReason;
	
	/**
	 * Gets the allergyStatus
	 *
	 * @return the allergyStatus
	 */
	public String getAllergyStatus() {
		return allergyStatus;
	}
	
	/**
	 * Sets the allergyStatus
	 *
	 * @param allergyStatus the allergyStatus to set
	 */
	public void setAllergyStatus(String allergyStatus) {
		this.allergyStatus = allergyStatus;
	}
	
	/**
	 * Gets the patientCreatorUuid
	 *
	 * @return the patientCreatorUuid
	 */
	public String getPatientCreatorUuid() {
		return patientCreatorUuid;
	}
	
	/**
	 * Sets the patientCreatorUuid
	 *
	 * @param patientCreatorUuid the patientCreatorUuid to set
	 */
	public void setPatientCreatorUuid(String patientCreatorUuid) {
		this.patientCreatorUuid = patientCreatorUuid;
	}
	
	/**
	 * Gets the patientDateCreated
	 *
	 * @return the patientDateCreated
	 */
	public LocalDateTime getPatientDateCreated() {
		return patientDateCreated;
	}
	
	/**
	 * Sets the patientDateCreated
	 *
	 * @param patientDateCreated the patientDateCreated to set
	 */
	public void setPatientDateCreated(LocalDateTime patientDateCreated) {
		this.patientDateCreated = patientDateCreated;
	}
	
	/**
	 * Gets the patientChangedByUuid
	 *
	 * @return the patientChangedByUuid
	 */
	public String getPatientChangedByUuid() {
		return patientChangedByUuid;
	}
	
	/**
	 * Sets the patientChangedByUuid
	 *
	 * @param patientChangedByUuid the patientChangedByUuid to set
	 */
	public void setPatientChangedByUuid(String patientChangedByUuid) {
		this.patientChangedByUuid = patientChangedByUuid;
	}
	
	/**
	 * Gets the patientDateChanged
	 *
	 * @return the patientDateChanged
	 */
	public LocalDateTime getPatientDateChanged() {
		return patientDateChanged;
	}
	
	/**
	 * Sets the patientDateChanged
	 *
	 * @param patientDateChanged the patientDateChanged to set
	 */
	public void setPatientDateChanged(LocalDateTime patientDateChanged) {
		this.patientDateChanged = patientDateChanged;
	}
	
	/**
	 * Gets the patientVoided
	 *
	 * @return the patientVoided
	 */
	public boolean isPatientVoided() {
		return patientVoided;
	}
	
	/**
	 * Sets the patientVoided
	 *
	 * @param patientVoided the patientVoided to set
	 */
	public void setPatientVoided(boolean patientVoided) {
		this.patientVoided = patientVoided;
	}
	
	/**
	 * Gets the patientVoidedByUuid
	 *
	 * @return the patientVoidedByUuid
	 */
	public String getPatientVoidedByUuid() {
		return patientVoidedByUuid;
	}
	
	/**
	 * Sets the patientVoidedByUuid
	 *
	 * @param patientVoidedByUuid the patientVoidedByUuid to set
	 */
	public void setPatientVoidedByUuid(String patientVoidedByUuid) {
		this.patientVoidedByUuid = patientVoidedByUuid;
	}
	
	/**
	 * Gets the patientDateVoided
	 *
	 * @return the patientDateVoided
	 */
	public LocalDateTime getPatientDateVoided() {
		return patientDateVoided;
	}
	
	/**
	 * Sets the patientDateVoided
	 *
	 * @param patientDateVoided the patientDateVoided to set
	 */
	public void setPatientDateVoided(LocalDateTime patientDateVoided) {
		this.patientDateVoided = patientDateVoided;
	}
	
	/**
	 * Gets the patientVoidReason
	 *
	 * @return the patientVoidReason
	 */
	public String getPatientVoidReason() {
		return patientVoidReason;
	}
	
	/**
	 * Sets the patientVoidReason
	 *
	 * @param patientVoidReason the patientVoidReason to set
	 */
	public void setPatientVoidReason(String patientVoidReason) {
		this.patientVoidReason = patientVoidReason;
	}
	
}
