package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class PatientIdentifierModel extends BaseChangeableDataModel {
	
	private String patientUuid;
	
	private String identifier;
	
	private String patientIdentifierTypeUuid;
	
	private boolean preferred;
	
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
	 * Gets the patientIdentifierTypeUuid
	 *
	 * @return the patientIdentifierTypeUuid
	 */
	public String getPatientIdentifierTypeUuid() {
		return patientIdentifierTypeUuid;
	}
	
	/**
	 * Sets the patientIdentifierTypeUuid
	 *
	 * @param patientIdentifierTypeUuid the patientIdentifierTypeUuid to set
	 */
	public void setPatientIdentifierTypeUuid(String patientIdentifierTypeUuid) {
		this.patientIdentifierTypeUuid = patientIdentifierTypeUuid;
	}
	
	/**
	 * Gets the preferred
	 *
	 * @return the preferred
	 */
	public boolean isPreferred() {
		return preferred;
	}
	
	/**
	 * Sets the preferred
	 *
	 * @param preferred the preferred to set
	 */
	public void setPreferred(boolean preferred) {
		this.preferred = preferred;
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
