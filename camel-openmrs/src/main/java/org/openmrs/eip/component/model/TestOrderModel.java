package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class TestOrderModel extends OrderModel {

    private String specimenSourceUuid;

    private String laterality;

    private String clinicalHistory;

    private String frequencyUuid;

    private Integer numberOfRepeats;

    /**
     * Gets the specimenSourceUuid
     *
     * @return the specimenSourceUuid
     */
    public String getSpecimenSourceUuid() {
        return specimenSourceUuid;
    }

    /**
     * Sets the specimenSourceUuid
     *
     * @param specimenSourceUuid the specimenSourceUuid to set
     */
    public void setSpecimenSourceUuid(String specimenSourceUuid) {
        this.specimenSourceUuid = specimenSourceUuid;
    }

    /**
     * Gets the laterality
     *
     * @return the laterality
     */
    public String getLaterality() {
        return laterality;
    }

    /**
     * Sets the laterality
     *
     * @param laterality the laterality to set
     */
    public void setLaterality(String laterality) {
        this.laterality = laterality;
    }

    /**
     * Gets the clinicalHistory
     *
     * @return the clinicalHistory
     */
    public String getClinicalHistory() {
        return clinicalHistory;
    }

    /**
     * Sets the clinicalHistory
     *
     * @param clinicalHistory the clinicalHistory to set
     */
    public void setClinicalHistory(String clinicalHistory) {
        this.clinicalHistory = clinicalHistory;
    }

    /**
     * Gets the frequencyUuid
     *
     * @return the frequencyUuid
     */
    public String getFrequencyUuid() {
        return frequencyUuid;
    }

    /**
     * Sets the frequencyUuid
     *
     * @param frequencyUuid the frequencyUuid to set
     */
    public void setFrequencyUuid(String frequencyUuid) {
        this.frequencyUuid = frequencyUuid;
    }

    /**
     * Gets the numberOfRepeats
     *
     * @return the numberOfRepeats
     */
    public Integer getNumberOfRepeats() {
        return numberOfRepeats;
    }

    /**
     * Sets the numberOfRepeats
     *
     * @param numberOfRepeats the numberOfRepeats to set
     */
    public void setNumberOfRepeats(Integer numberOfRepeats) {
        this.numberOfRepeats = numberOfRepeats;
    }
}
