package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class DrugOrderModel extends OrderModel {

    private Double dose;

    private String doseUnitsUuid;

    private Boolean asNeeded;

    private Double quantity;

    private String quantityUnitsUuid;

    private String drugUuid;

    private String asNeededCondition;

    private Integer numRefills;

    private String dosingInstructions;

    private Integer duration;

    private String durationUnitsUuid;

    private String routeUuid;

    private String drugNonCoded;

    private String brandName;

    private Boolean dispenseAsWritten;

    private String frequencyUuid;

    private String dosingType;

    /**
     * Gets the dose
     *
     * @return the dose
     */
    public Double getDose() {
        return dose;
    }

    /**
     * Sets the dose
     *
     * @param dose the dose to set
     */
    public void setDose(Double dose) {
        this.dose = dose;
    }

    /**
     * Gets the doseUnitsUuid
     *
     * @return the doseUnitsUuid
     */
    public String getDoseUnitsUuid() {
        return doseUnitsUuid;
    }

    /**
     * Sets the doseUnitsUuid
     *
     * @param doseUnitsUuid the doseUnitsUuid to set
     */
    public void setDoseUnitsUuid(String doseUnitsUuid) {
        this.doseUnitsUuid = doseUnitsUuid;
    }

    /**
     * Gets the asNeeded
     *
     * @return the asNeeded
     */
    public Boolean getAsNeeded() {
        return asNeeded;
    }

    /**
     * Sets the asNeeded
     *
     * @param asNeeded the asNeeded to set
     */
    public void setAsNeeded(Boolean asNeeded) {
        this.asNeeded = asNeeded;
    }

    /**
     * Gets the quantity
     *
     * @return the quantity
     */
    public Double getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity
     *
     * @param quantity the quantity to set
     */
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    /**
     * Gets the quantityUnitsUuid
     *
     * @return the quantityUnitsUuid
     */
    public String getQuantityUnitsUuid() {
        return quantityUnitsUuid;
    }

    /**
     * Sets the quantityUnitsUuid
     *
     * @param quantityUnitsUuid the quantityUnitsUuid to set
     */
    public void setQuantityUnitsUuid(String quantityUnitsUuid) {
        this.quantityUnitsUuid = quantityUnitsUuid;
    }

    /**
     * Gets the drugUuid
     *
     * @return the drugUuid
     */
    public String getDrugUuid() {
        return drugUuid;
    }

    /**
     * Sets the drugUuid
     *
     * @param drugUuid the drugUuid to set
     */
    public void setDrugUuid(String drugUuid) {
        this.drugUuid = drugUuid;
    }

    /**
     * Gets the asNeededCondition
     *
     * @return the asNeededCondition
     */
    public String getAsNeededCondition() {
        return asNeededCondition;
    }

    /**
     * Sets the asNeededCondition
     *
     * @param asNeededCondition the asNeededCondition to set
     */
    public void setAsNeededCondition(String asNeededCondition) {
        this.asNeededCondition = asNeededCondition;
    }

    /**
     * Gets the numRefills
     *
     * @return the numRefills
     */
    public Integer getNumRefills() {
        return numRefills;
    }

    /**
     * Sets the numRefills
     *
     * @param numRefills the numRefills to set
     */
    public void setNumRefills(Integer numRefills) {
        this.numRefills = numRefills;
    }

    /**
     * Gets the dosingInstructions
     *
     * @return the dosingInstructions
     */
    public String getDosingInstructions() {
        return dosingInstructions;
    }

    /**
     * Sets the dosingInstructions
     *
     * @param dosingInstructions the dosingInstructions to set
     */
    public void setDosingInstructions(String dosingInstructions) {
        this.dosingInstructions = dosingInstructions;
    }

    /**
     * Gets the duration
     *
     * @return the duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Sets the duration
     *
     * @param duration the duration to set
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    /**
     * Gets the durationUnitsUuid
     *
     * @return the durationUnitsUuid
     */
    public String getDurationUnitsUuid() {
        return durationUnitsUuid;
    }

    /**
     * Sets the durationUnitsUuid
     *
     * @param durationUnitsUuid the durationUnitsUuid to set
     */
    public void setDurationUnitsUuid(String durationUnitsUuid) {
        this.durationUnitsUuid = durationUnitsUuid;
    }

    /**
     * Gets the routeUuid
     *
     * @return the routeUuid
     */
    public String getRouteUuid() {
        return routeUuid;
    }

    /**
     * Sets the routeUuid
     *
     * @param routeUuid the routeUuid to set
     */
    public void setRouteUuid(String routeUuid) {
        this.routeUuid = routeUuid;
    }

    /**
     * Gets the drugNonCoded
     *
     * @return the drugNonCoded
     */
    public String getDrugNonCoded() {
        return drugNonCoded;
    }

    /**
     * Sets the drugNonCoded
     *
     * @param drugNonCoded the drugNonCoded to set
     */
    public void setDrugNonCoded(String drugNonCoded) {
        this.drugNonCoded = drugNonCoded;
    }

    /**
     * Gets the brandName
     *
     * @return the brandName
     */
    public String getBrandName() {
        return brandName;
    }

    /**
     * Sets the brandName
     *
     * @param brandName the brandName to set
     */
    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    /**
     * Gets the dispenseAsWritten
     *
     * @return the dispenseAsWritten
     */
    public Boolean getDispenseAsWritten() {
        return dispenseAsWritten;
    }

    /**
     * Sets the dispenseAsWritten
     *
     * @param dispenseAsWritten the dispenseAsWritten to set
     */
    public void setDispenseAsWritten(Boolean dispenseAsWritten) {
        this.dispenseAsWritten = dispenseAsWritten;
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
     * Gets the dosingType
     *
     * @return the dosingType
     */
    public String getDosingType() {
        return dosingType;
    }

    /**
     * Sets the dosingType
     *
     * @param dosingType the dosingType to set
     */
    public void setDosingType(String dosingType) {
        this.dosingType = dosingType;
    }
}
