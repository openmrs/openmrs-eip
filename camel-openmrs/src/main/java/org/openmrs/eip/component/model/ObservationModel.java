package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class ObservationModel extends BaseDataModel {

    private String personUuid;

    private String conceptUuid;

    private String encounterUuid;

    private String orderUuid;

    private LocalDateTime obsDatetime;

    private String locationUuid;

    private String obsGroupUuid;

    private String accessionNumber;

    private Long valueGroupId;

    private String valueCodedUuid;

    private String valueCodedNameUuid;

    private String valueDrugUuid;

    private LocalDateTime valueDatetime;

    private Double valueNumeric;

    private Integer valueModifier;

    private String valueText;

    private String valueComplex;

    private String comments;

    private String previousVersionUuid;

    private String formNamespaceAndPath;

    private String status;

    private String interpretation;

    /**
     * Gets the personUuid
     *
     * @return the personUuid
     */
    public String getPersonUuid() {
        return personUuid;
    }

    /**
     * Sets the personUuid
     *
     * @param personUuid the personUuid to set
     */
    public void setPersonUuid(String personUuid) {
        this.personUuid = personUuid;
    }

    /**
     * Gets the conceptUuid
     *
     * @return the conceptUuid
     */
    public String getConceptUuid() {
        return conceptUuid;
    }

    /**
     * Sets the conceptUuid
     *
     * @param conceptUuid the conceptUuid to set
     */
    public void setConceptUuid(String conceptUuid) {
        this.conceptUuid = conceptUuid;
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
     * Gets the orderUuid
     *
     * @return the orderUuid
     */
    public String getOrderUuid() {
        return orderUuid;
    }

    /**
     * Sets the orderUuid
     *
     * @param orderUuid the orderUuid to set
     */
    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    /**
     * Gets the obsDatetime
     *
     * @return the obsDatetime
     */
    public LocalDateTime getObsDatetime() {
        return obsDatetime;
    }

    /**
     * Sets the obsDatetime
     *
     * @param obsDatetime the obsDatetime to set
     */
    public void setObsDatetime(LocalDateTime obsDatetime) {
        this.obsDatetime = obsDatetime;
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
     * Gets the obsGroupUuid
     *
     * @return the obsGroupUuid
     */
    public String getObsGroupUuid() {
        return obsGroupUuid;
    }

    /**
     * Sets the obsGroupUuid
     *
     * @param obsGroupUuid the obsGroupUuid to set
     */
    public void setObsGroupUuid(String obsGroupUuid) {
        this.obsGroupUuid = obsGroupUuid;
    }

    /**
     * Gets the accessionNumber
     *
     * @return the accessionNumber
     */
    public String getAccessionNumber() {
        return accessionNumber;
    }

    /**
     * Sets the accessionNumber
     *
     * @param accessionNumber the accessionNumber to set
     */
    public void setAccessionNumber(String accessionNumber) {
        this.accessionNumber = accessionNumber;
    }

    /**
     * Gets the valueGroupId
     *
     * @return the valueGroupId
     */
    public Long getValueGroupId() {
        return valueGroupId;
    }

    /**
     * Sets the valueGroupId
     *
     * @param valueGroupId the valueGroupId to set
     */
    public void setValueGroupId(Long valueGroupId) {
        this.valueGroupId = valueGroupId;
    }

    /**
     * Gets the valueCodedUuid
     *
     * @return the valueCodedUuid
     */
    public String getValueCodedUuid() {
        return valueCodedUuid;
    }

    /**
     * Sets the valueCodedUuid
     *
     * @param valueCodedUuid the valueCodedUuid to set
     */
    public void setValueCodedUuid(String valueCodedUuid) {
        this.valueCodedUuid = valueCodedUuid;
    }

    /**
     * Gets the valueCodedNameUuid
     *
     * @return the valueCodedNameUuid
     */
    public String getValueCodedNameUuid() {
        return valueCodedNameUuid;
    }

    /**
     * Sets the valueCodedNameUuid
     *
     * @param valueCodedNameUuid the valueCodedNameUuid to set
     */
    public void setValueCodedNameUuid(String valueCodedNameUuid) {
        this.valueCodedNameUuid = valueCodedNameUuid;
    }

    /**
     * Gets the valueDrugUuid
     *
     * @return the valueDrugUuid
     */
    public String getValueDrugUuid() {
        return valueDrugUuid;
    }

    /**
     * Sets the valueDrugUuid
     *
     * @param valueDrugUuid the valueDrugUuid to set
     */
    public void setValueDrugUuid(String valueDrugUuid) {
        this.valueDrugUuid = valueDrugUuid;
    }

    /**
     * Gets the valueDatetime
     *
     * @return the valueDatetime
     */
    public LocalDateTime getValueDatetime() {
        return valueDatetime;
    }

    /**
     * Sets the valueDatetime
     *
     * @param valueDatetime the valueDatetime to set
     */
    public void setValueDatetime(LocalDateTime valueDatetime) {
        this.valueDatetime = valueDatetime;
    }

    /**
     * Gets the valueNumeric
     *
     * @return the valueNumeric
     */
    public Double getValueNumeric() {
        return valueNumeric;
    }

    /**
     * Sets the valueNumeric
     *
     * @param valueNumeric the valueNumeric to set
     */
    public void setValueNumeric(Double valueNumeric) {
        this.valueNumeric = valueNumeric;
    }

    /**
     * Gets the valueModifier
     *
     * @return the valueModifier
     */
    public Integer getValueModifier() {
        return valueModifier;
    }

    /**
     * Sets the valueModifier
     *
     * @param valueModifier the valueModifier to set
     */
    public void setValueModifier(Integer valueModifier) {
        this.valueModifier = valueModifier;
    }

    /**
     * Gets the valueText
     *
     * @return the valueText
     */
    public String getValueText() {
        return valueText;
    }

    /**
     * Sets the valueText
     *
     * @param valueText the valueText to set
     */
    public void setValueText(String valueText) {
        this.valueText = valueText;
    }

    /**
     * Gets the valueComplex
     *
     * @return the valueComplex
     */
    public String getValueComplex() {
        return valueComplex;
    }

    /**
     * Sets the valueComplex
     *
     * @param valueComplex the valueComplex to set
     */
    public void setValueComplex(String valueComplex) {
        this.valueComplex = valueComplex;
    }

    /**
     * Gets the comments
     *
     * @return the comments
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments
     *
     * @param comments the comments to set
     */
    public void setComments(String comments) {
        this.comments = comments;
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
     * Gets the formNamespaceAndPath
     *
     * @return the formNamespaceAndPath
     */
    public String getFormNamespaceAndPath() {
        return formNamespaceAndPath;
    }

    /**
     * Sets the formNamespaceAndPath
     *
     * @param formNamespaceAndPath the formNamespaceAndPath to set
     */
    public void setFormNamespaceAndPath(String formNamespaceAndPath) {
        this.formNamespaceAndPath = formNamespaceAndPath;
    }

    /**
     * Gets the status
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the status
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the interpretation
     *
     * @return the interpretation
     */
    public String getInterpretation() {
        return interpretation;
    }

    /**
     * Sets the interpretation
     *
     * @param interpretation the interpretation to set
     */
    public void setInterpretation(String interpretation) {
        this.interpretation = interpretation;
    }
}
