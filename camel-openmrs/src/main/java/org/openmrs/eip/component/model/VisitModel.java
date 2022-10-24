package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class VisitModel extends BaseChangeableDataModel {
	
	private String patientUuid;
	
	private String visitTypeUuid;
	
	private LocalDateTime dateStarted;
	
	private LocalDateTime dateStopped;
	
	private String indicationConceptUuid;
	
	private String locationUuid;
	
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
	 * Gets the visitTypeUuid
	 *
	 * @return the visitTypeUuid
	 */
	public String getVisitTypeUuid() {
		return visitTypeUuid;
	}
	
	/**
	 * Sets the visitTypeUuid
	 *
	 * @param visitTypeUuid the visitTypeUuid to set
	 */
	public void setVisitTypeUuid(String visitTypeUuid) {
		this.visitTypeUuid = visitTypeUuid;
	}
	
	/**
	 * Gets the dateStarted
	 *
	 * @return the dateStarted
	 */
	public LocalDateTime getDateStarted() {
		return dateStarted;
	}
	
	/**
	 * Sets the dateStarted
	 *
	 * @param dateStarted the dateStarted to set
	 */
	public void setDateStarted(LocalDateTime dateStarted) {
		this.dateStarted = dateStarted;
	}
	
	/**
	 * Gets the dateStopped
	 *
	 * @return the dateStopped
	 */
	public LocalDateTime getDateStopped() {
		return dateStopped;
	}
	
	/**
	 * Sets the dateStopped
	 *
	 * @param dateStopped the dateStopped to set
	 */
	public void setDateStopped(LocalDateTime dateStopped) {
		this.dateStopped = dateStopped;
	}
	
	/**
	 * Gets the indicationConceptUuid
	 *
	 * @return the indicationConceptUuid
	 */
	public String getIndicationConceptUuid() {
		return indicationConceptUuid;
	}
	
	/**
	 * Sets the indicationConceptUuid
	 *
	 * @param indicationConceptUuid the indicationConceptUuid to set
	 */
	public void setIndicationConceptUuid(String indicationConceptUuid) {
		this.indicationConceptUuid = indicationConceptUuid;
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
	
}
