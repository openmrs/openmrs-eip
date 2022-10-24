package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class EncounterDiagnosisModel extends BaseChangeableDataModel {
	
	private String diagnosisCodedUuid;
	
	private String diagnosisNonCoded;
	
	private String diagnosisCodedNameUuid;
	
	private String encounterUuid;
	
	private String patientUuid;
	
	private String conditionUuid;
	
	private String certainty;
	
	private int rank;
	
	/**
	 * Gets the diagnosisCodedUuid
	 *
	 * @return the diagnosisCodedUuid
	 */
	public String getDiagnosisCodedUuid() {
		return diagnosisCodedUuid;
	}
	
	/**
	 * Sets the diagnosisCodedUuid
	 *
	 * @param diagnosisCodedUuid the diagnosisCodedUuid to set
	 */
	public void setDiagnosisCodedUuid(String diagnosisCodedUuid) {
		this.diagnosisCodedUuid = diagnosisCodedUuid;
	}
	
	/**
	 * Gets the diagnosisNonCoded
	 *
	 * @return the diagnosisNonCoded
	 */
	public String getDiagnosisNonCoded() {
		return diagnosisNonCoded;
	}
	
	/**
	 * Sets the diagnosisNonCoded
	 *
	 * @param diagnosisNonCoded the diagnosisNonCoded to set
	 */
	public void setDiagnosisNonCoded(String diagnosisNonCoded) {
		this.diagnosisNonCoded = diagnosisNonCoded;
	}
	
	/**
	 * Gets the diagnosisCodedNameUuid
	 *
	 * @return the diagnosisCodedNameUuid
	 */
	public String getDiagnosisCodedNameUuid() {
		return diagnosisCodedNameUuid;
	}
	
	/**
	 * Sets the diagnosisCodedNameUuid
	 *
	 * @param diagnosisCodedNameUuid the diagnosisCodedNameUuid to set
	 */
	public void setDiagnosisCodedNameUuid(String diagnosisCodedNameUuid) {
		this.diagnosisCodedNameUuid = diagnosisCodedNameUuid;
	}
	
	/**
	 * Gets the encounterUuid
	 *
	 * @return the encounterUuid
	 */
	public String getEncounterUuid() {
		return encounterUuid;
	}
	
	/**
	 * Sets the encounterUuid
	 *
	 * @param encounterUuid the encounterUuid to set
	 */
	public void setEncounterUuid(String encounterUuid) {
		this.encounterUuid = encounterUuid;
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
	 * Gets the conditionUuid
	 *
	 * @return the conditionUuid
	 */
	public String getConditionUuid() {
		return conditionUuid;
	}
	
	/**
	 * Sets the conditionUuid
	 *
	 * @param conditionUuid the conditionUuid to set
	 */
	public void setConditionUuid(String conditionUuid) {
		this.conditionUuid = conditionUuid;
	}
	
	/**
	 * Gets the certainty
	 *
	 * @return the certainty
	 */
	public String getCertainty() {
		return certainty;
	}
	
	/**
	 * Sets the certainty
	 *
	 * @param certainty the certainty to set
	 */
	public void setCertainty(String certainty) {
		this.certainty = certainty;
	}
	
	/**
	 * Gets the rank
	 *
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}
	
	/**
	 * Sets the rank
	 *
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}
}
