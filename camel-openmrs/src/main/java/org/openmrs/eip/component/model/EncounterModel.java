package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class EncounterModel extends BaseChangeableDataModel {
	
	private String encounterTypeUuid;
	
	private String patientUuid;
	
	private String locationUuid;
	
	private String formUuid;
	
	private LocalDateTime encounterDatetime;
	
	private String visitUuid;
	
	/**
	 * Gets the encounterTypeUuid
	 *
	 * @return the encounterTypeUuid
	 */
	public String getEncounterTypeUuid() {
		return encounterTypeUuid;
	}
	
	/**
	 * Sets the encounterTypeUuid
	 *
	 * @param encounterTypeUuid the encounterTypeUuid to set
	 */
	public void setEncounterTypeUuid(String encounterTypeUuid) {
		this.encounterTypeUuid = encounterTypeUuid;
	}
	
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
	 * Gets the formUuid
	 *
	 * @return the formUuid
	 */
	public String getFormUuid() {
		return formUuid;
	}
	
	/**
	 * Sets the formUuid
	 *
	 * @param formUuid the formUuid to set
	 */
	public void setFormUuid(String formUuid) {
		this.formUuid = formUuid;
	}
	
	/**
	 * Gets the encounterDatetime
	 *
	 * @return the encounterDatetime
	 */
	public LocalDateTime getEncounterDatetime() {
		return encounterDatetime;
	}
	
	/**
	 * Sets the encounterDatetime
	 *
	 * @param encounterDatetime the encounterDatetime to set
	 */
	public void setEncounterDatetime(LocalDateTime encounterDatetime) {
		this.encounterDatetime = encounterDatetime;
	}
	
	/**
	 * Gets the visitUuid
	 *
	 * @return the visitUuid
	 */
	public String getVisitUuid() {
		return visitUuid;
	}
	
	/**
	 * Sets the visitUuid
	 *
	 * @param visitUuid the visitUuid to set
	 */
	public void setVisitUuid(String visitUuid) {
		this.visitUuid = visitUuid;
	}
}
