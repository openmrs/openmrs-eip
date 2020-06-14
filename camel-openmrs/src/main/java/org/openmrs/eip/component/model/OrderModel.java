package org.openmrs.eip.component.model;

import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
public class OrderModel extends BaseDataModel {

    private String patientUuid;

    private String orderTypeUuid;

    private String conceptUuid;

    private String instructions;

    private LocalDateTime dateActivated;

    private LocalDateTime autoExpireDate;

    private String encounterUuid;

    private String ordererUuid;

    private LocalDateTime dateStopped;

    private String orderReasonUuid;

    private String accessionNumber;

    private String orderReasonNonCoded;

    private String urgency;

    private String orderNumber;

    private String commentToFulfiller;

    private String careSettingUuid;

    private LocalDateTime scheduledDate;

    private Double sortWeight;

    private String previousOrderUuid;

    private String action;

    private String orderGroupUuid;

    private String fulfillerStatus;

    private String fulfillerComment;

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
     * Gets the orderTypeUuid
     *
     * @return the orderTypeUuid
     */
    public String getOrderTypeUuid() {
        return orderTypeUuid;
    }

    /**
     * Sets the orderTypeUuid
     *
     * @param orderTypeUuid the orderTypeUuid to set
     */
    public void setOrderTypeUuid(String orderTypeUuid) {
        this.orderTypeUuid = orderTypeUuid;
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
     * Gets the instructions
     *
     * @return the instructions
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the instructions
     *
     * @param instructions the instructions to set
     */
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * Gets the dateActivated
     *
     * @return the dateActivated
     */
    public LocalDateTime getDateActivated() {
        return dateActivated;
    }

    /**
     * Sets the dateActivated
     *
     * @param dateActivated the dateActivated to set
     */
    public void setDateActivated(LocalDateTime dateActivated) {
        this.dateActivated = dateActivated;
    }

    /**
     * Gets the autoExpireDate
     *
     * @return the autoExpireDate
     */
    public LocalDateTime getAutoExpireDate() {
        return autoExpireDate;
    }

    /**
     * Sets the autoExpireDate
     *
     * @param autoExpireDate the autoExpireDate to set
     */
    public void setAutoExpireDate(LocalDateTime autoExpireDate) {
        this.autoExpireDate = autoExpireDate;
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
     * Gets the ordererUuid
     *
     * @return the ordererUuid
     */
    public String getOrdererUuid() {
        return ordererUuid;
    }

    /**
     * Sets the ordererUuid
     *
     * @param ordererUuid the ordererUuid to set
     */
    public void setOrdererUuid(String ordererUuid) {
        this.ordererUuid = ordererUuid;
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
     * Gets the orderReasonUuid
     *
     * @return the orderReasonUuid
     */
    public String getOrderReasonUuid() {
        return orderReasonUuid;
    }

    /**
     * Sets the orderReasonUuid
     *
     * @param orderReasonUuid the orderReasonUuid to set
     */
    public void setOrderReasonUuid(String orderReasonUuid) {
        this.orderReasonUuid = orderReasonUuid;
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
     * Gets the orderReasonNonCoded
     *
     * @return the orderReasonNonCoded
     */
    public String getOrderReasonNonCoded() {
        return orderReasonNonCoded;
    }

    /**
     * Sets the orderReasonNonCoded
     *
     * @param orderReasonNonCoded the orderReasonNonCoded to set
     */
    public void setOrderReasonNonCoded(String orderReasonNonCoded) {
        this.orderReasonNonCoded = orderReasonNonCoded;
    }

    /**
     * Gets the urgency
     *
     * @return the urgency
     */
    public String getUrgency() {
        return urgency;
    }

    /**
     * Sets the urgency
     *
     * @param urgency the urgency to set
     */
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    /**
     * Gets the orderNumber
     *
     * @return the orderNumber
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Sets the orderNumber
     *
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    /**
     * Gets the commentToFulfiller
     *
     * @return the commentToFulfiller
     */
    public String getCommentToFulfiller() {
        return commentToFulfiller;
    }

    /**
     * Sets the commentToFulfiller
     *
     * @param commentToFulfiller the commentToFulfiller to set
     */
    public void setCommentToFulfiller(String commentToFulfiller) {
        this.commentToFulfiller = commentToFulfiller;
    }

    /**
     * Gets the careSettingUuid
     *
     * @return the careSettingUuid
     */
    public String getCareSettingUuid() {
        return careSettingUuid;
    }

    /**
     * Sets the careSettingUuid
     *
     * @param careSettingUuid the careSettingUuid to set
     */
    public void setCareSettingUuid(String careSettingUuid) {
        this.careSettingUuid = careSettingUuid;
    }

    /**
     * Gets the scheduledDate
     *
     * @return the scheduledDate
     */
    public LocalDateTime getScheduledDate() {
        return scheduledDate;
    }

    /**
     * Sets the scheduledDate
     *
     * @param scheduledDate the scheduledDate to set
     */
    public void setScheduledDate(LocalDateTime scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    /**
     * Gets the sortWeight
     *
     * @return the sortWeight
     */
    public Double getSortWeight() {
        return sortWeight;
    }

    /**
     * Sets the sortWeight
     *
     * @param sortWeight the sortWeight to set
     */
    public void setSortWeight(Double sortWeight) {
        this.sortWeight = sortWeight;
    }

    /**
     * Gets the previousOrderUuid
     *
     * @return the previousOrderUuid
     */
    public String getPreviousOrderUuid() {
        return previousOrderUuid;
    }

    /**
     * Sets the previousOrderUuid
     *
     * @param previousOrderUuid the previousOrderUuid to set
     */
    public void setPreviousOrderUuid(String previousOrderUuid) {
        this.previousOrderUuid = previousOrderUuid;
    }

    /**
     * Gets the action
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action
     *
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the orderGroupUuid
     *
     * @return the orderGroupUuid
     */
    public String getOrderGroupUuid() {
        return orderGroupUuid;
    }

    /**
     * Sets the orderGroupUuid
     *
     * @param orderGroupUuid the orderGroupUuid to set
     */
    public void setOrderGroupUuid(String orderGroupUuid) {
        this.orderGroupUuid = orderGroupUuid;
    }

    /**
     * Gets the fulfillerStatus
     *
     * @return the fulfillerStatus
     */
    public String getFulfillerStatus() {
        return fulfillerStatus;
    }

    /**
     * Sets the fulfillerStatus
     *
     * @param fulfillerStatus the fulfillerStatus to set
     */
    public void setFulfillerStatus(String fulfillerStatus) {
        this.fulfillerStatus = fulfillerStatus;
    }

    /**
     * Gets the fulfillerComment
     *
     * @return the fulfillerComment
     */
    public String getFulfillerComment() {
        return fulfillerComment;
    }

    /**
     * Sets the fulfillerComment
     *
     * @param fulfillerComment the fulfillerComment to set
     */
    public void setFulfillerComment(String fulfillerComment) {
        this.fulfillerComment = fulfillerComment;
    }
}
