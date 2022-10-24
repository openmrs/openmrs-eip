package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class PatientProgramModel extends BaseChangeableDataModel {
	
	private String patientUuid;
	
	private String programUuid;
	
	private LocalDateTime dateEnrolled;
	
	private LocalDateTime dateCompleted;
	
	private String locationUuid;
	
	private String outcomeConceptUuid;
	
	/**
	 * Gets the patientUuid
	 *
	 * @return the patientUuid
	 */
	public String getPatientUuid() {
		return patientUuid;
	}
	
	/**
	 * Sets the patientUuid
	 *
	 * @param patientUuid the patientUuid to set
	 */
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	/**
	 * Gets the programUuid
	 *
	 * @return the programUuid
	 */
	public String getProgramUuid() {
		return programUuid;
	}
	
	/**
	 * Sets the programUuid
	 *
	 * @param programUuid the programUuid to set
	 */
	public void setProgramUuid(String programUuid) {
		this.programUuid = programUuid;
	}
	
	/**
	 * Gets the dateEnrolled
	 *
	 * @return the dateEnrolled
	 */
	public LocalDateTime getDateEnrolled() {
		return dateEnrolled;
	}
	
	/**
	 * Sets the dateEnrolled
	 *
	 * @param dateEnrolled the dateEnrolled to set
	 */
	public void setDateEnrolled(LocalDateTime dateEnrolled) {
		this.dateEnrolled = dateEnrolled;
	}
	
	/**
	 * Gets the dateCompleted
	 *
	 * @return the dateCompleted
	 */
	public LocalDateTime getDateCompleted() {
		return dateCompleted;
	}
	
	/**
	 * Sets the dateCompleted
	 *
	 * @param dateCompleted the dateCompleted to set
	 */
	public void setDateCompleted(LocalDateTime dateCompleted) {
		this.dateCompleted = dateCompleted;
	}
	
	/**
	 * Gets the locationUuid
	 *
	 * @return the locationUuid
	 */
	public String getLocationUuid() {
		return locationUuid;
	}
	
	/**
	 * Sets the locationUuid
	 *
	 * @param locationUuid the locationUuid to set
	 */
	public void setLocationUuid(String locationUuid) {
		this.locationUuid = locationUuid;
	}
	
	/**
	 * Gets the outcomeConceptUuid
	 *
	 * @return the outcomeConceptUuid
	 */
	public String getOutcomeConceptUuid() {
		return outcomeConceptUuid;
	}
	
	/**
	 * Sets the outcomeConceptUuid
	 *
	 * @param outcomeConceptUuid the outcomeConceptUuid to set
	 */
	public void setOutcomeConceptUuid(String outcomeConceptUuid) {
		this.outcomeConceptUuid = outcomeConceptUuid;
	}
}
