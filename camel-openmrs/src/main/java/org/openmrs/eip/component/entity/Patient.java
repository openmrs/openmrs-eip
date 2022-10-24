package org.openmrs.eip.component.entity;

import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.UserLight;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "patient_id")
public class Patient extends Person {
	
	@NotNull
	@Column(name = "allergy_status")
	private String allergyStatus;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "creator")
	private UserLight patientCreator;
	
	@NotNull
	@Column(name = "date_created")
	private LocalDateTime patientDateCreated;
	
	@ManyToOne
	@JoinColumn(name = "changed_by")
	private UserLight patientChangedBy;
	
	@Column(name = "date_changed")
	private LocalDateTime patientDateChanged;
	
	@NotNull
	@Column(name = "voided")
	private boolean patientVoided;
	
	@ManyToOne
	@JoinColumn(name = "voided_by")
	private UserLight patientVoidedBy;
	
	@Column(name = "date_voided")
	private LocalDateTime patientDateVoided;
	
	@Column(name = "void_reason")
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
	 * Gets the patientCreator
	 *
	 * @return the patientCreator
	 */
	public UserLight getPatientCreator() {
		return patientCreator;
	}
	
	/**
	 * Sets the patientCreator
	 *
	 * @param patientCreator the patientCreator to set
	 */
	public void setPatientCreator(UserLight patientCreator) {
		this.patientCreator = patientCreator;
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
	 * Gets the patientChangedBy
	 *
	 * @return the patientChangedBy
	 */
	public UserLight getPatientChangedBy() {
		return patientChangedBy;
	}
	
	/**
	 * Sets the patientChangedBy
	 *
	 * @param patientChangedBy the patientChangedBy to set
	 */
	public void setPatientChangedBy(UserLight patientChangedBy) {
		this.patientChangedBy = patientChangedBy;
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
	 * Gets the patientVoidedBy
	 *
	 * @return the patientVoidedBy
	 */
	public UserLight getPatientVoidedBy() {
		return patientVoidedBy;
	}
	
	/**
	 * Sets the patientVoidedBy
	 *
	 * @param patientVoidedBy the patientVoidedBy to set
	 */
	public void setPatientVoidedBy(UserLight patientVoidedBy) {
		this.patientVoidedBy = patientVoidedBy;
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
