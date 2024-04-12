package org.openmrs.eip.component.model;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = true)
public class PatientStateModel extends BaseChangeableDataModel {
	
	private String patientProgramUuid;
	
	private String stateUuid;
	
	private LocalDate startDate;
	
	private LocalDate endDate;
	
	@Getter
	@Setter
	private String encounterUuid;
	
	@Getter
	@Setter
	private String formNamespaceAndPath;
	
	/**
	 * Gets the patientProgramUuid
	 *
	 * @return the patientProgramUuid
	 */
	public String getPatientProgramUuid() {
		return patientProgramUuid;
	}
	
	/**
	 * Sets the patientProgramUuid
	 *
	 * @param patientProgramUuid the patientProgramUuid to set
	 */
	public void setPatientProgramUuid(String patientProgramUuid) {
		this.patientProgramUuid = patientProgramUuid;
	}
	
	/**
	 * Gets the stateUuid
	 *
	 * @return the stateUuid
	 */
	public String getStateUuid() {
		return stateUuid;
	}
	
	/**
	 * Sets the stateUuid
	 *
	 * @param stateUuid the stateUuid to set
	 */
	public void setStateUuid(String stateUuid) {
		this.stateUuid = stateUuid;
	}
	
	/**
	 * Gets the startDate
	 *
	 * @return the startDate
	 */
	public LocalDate getStartDate() {
		return startDate;
	}
	
	/**
	 * Sets the startDate
	 *
	 * @param startDate the startDate to set
	 */
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Gets the endDate
	 *
	 * @return the endDate
	 */
	public LocalDate getEndDate() {
		return endDate;
	}
	
	/**
	 * Sets the endDate
	 *
	 * @param endDate the endDate to set
	 */
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
