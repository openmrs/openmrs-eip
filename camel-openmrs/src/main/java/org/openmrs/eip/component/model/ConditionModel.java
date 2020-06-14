package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class ConditionModel extends BaseChangeableDataModel {

    private String additionalDetail;

    private String previousVersionUuid;

    private String conditionCodedUuid;

    private String conditionNonCoded;

    private String conditionCodedNameUuid;

    private String clinicalStatus;

    private String verificationStatus;

    private LocalDateTime onsetDate;

    private String patientUuid;

    private LocalDateTime endDate;

    /**
     * Gets the additionalDetail
     *
     * @return the additionalDetail
     */
    public String getAdditionalDetail() {
        return additionalDetail;
    }

    /**
     * Sets the additionalDetail
     *
     * @param additionalDetail the additionalDetail to set
     */
    public void setAdditionalDetail(String additionalDetail) {
        this.additionalDetail = additionalDetail;
    }

    /**
     * Gets the previousVersionUuid
     *
     * @return the previousVersionUuid
     */
    public String getPreviousVersionUuid() {
        return previousVersionUuid;
    }

    /**
     * Sets the previousVersionUuid
     *
     * @param previousVersionUuid the previousVersionUuid to set
     */
    public void setPreviousVersionUuid(String previousVersionUuid) {
        this.previousVersionUuid = previousVersionUuid;
    }

    /**
     * Gets the conditionCodedUuid
     *
     * @return the conditionCodedUuid
     */
    public String getConditionCodedUuid() {
        return conditionCodedUuid;
    }

    /**
     * Sets the conditionCodedUuid
     *
     * @param conditionCodedUuid the conditionCodedUuid to set
     */
    public void setConditionCodedUuid(String conditionCodedUuid) {
        this.conditionCodedUuid = conditionCodedUuid;
    }

    /**
     * Gets the conditionNonCoded
     *
     * @return the conditionNonCoded
     */
    public String getConditionNonCoded() {
        return conditionNonCoded;
    }

    /**
     * Sets the conditionNonCoded
     *
     * @param conditionNonCoded the conditionNonCoded to set
     */
    public void setConditionNonCoded(String conditionNonCoded) {
        this.conditionNonCoded = conditionNonCoded;
    }

    /**
     * Gets the conditionCodedNameUuid
     *
     * @return the conditionCodedNameUuid
     */
    public String getConditionCodedNameUuid() {
        return conditionCodedNameUuid;
    }

    /**
     * Sets the conditionCodedNameUuid
     *
     * @param conditionCodedNameUuid the conditionCodedNameUuid to set
     */
    public void setConditionCodedNameUuid(String conditionCodedNameUuid) {
        this.conditionCodedNameUuid = conditionCodedNameUuid;
    }

    /**
     * Gets the clinicalStatus
     *
     * @return the clinicalStatus
     */
    public String getClinicalStatus() {
        return clinicalStatus;
    }

    /**
     * Sets the clinicalStatus
     *
     * @param clinicalStatus the clinicalStatus to set
     */
    public void setClinicalStatus(String clinicalStatus) {
        this.clinicalStatus = clinicalStatus;
    }

    /**
     * Gets the verificationStatus
     *
     * @return the verificationStatus
     */
    public String getVerificationStatus() {
        return verificationStatus;
    }

    /**
     * Sets the verificationStatus
     *
     * @param verificationStatus the verificationStatus to set
     */
    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    /**
     * Gets the onsetDate
     *
     * @return the onsetDate
     */
    public LocalDateTime getOnsetDate() {
        return onsetDate;
    }

    /**
     * Sets the onsetDate
     *
     * @param onsetDate the onsetDate to set
     */
    public void setOnsetDate(LocalDateTime onsetDate) {
        this.onsetDate = onsetDate;
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
     * Gets the endDate
     *
     * @return the endDate
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }

    /**
     * Sets the endDate
     *
     * @param endDate the endDate to set
     */
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
}
