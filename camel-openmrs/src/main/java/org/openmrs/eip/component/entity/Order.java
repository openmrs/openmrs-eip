package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.OrderGroupLight;
import org.openmrs.eip.component.entity.light.OrderLight;
import org.openmrs.eip.component.entity.light.OrderTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.ProviderLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
@Inheritance(strategy = InheritanceType.JOINED)
public class Order extends BaseDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "order_type_id")
	private OrderTypeLight orderType;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "concept_id")
	private ConceptLight concept;
	
	private String instructions;
	
	@Column(name = "date_activated")
	private LocalDateTime dateActivated;
	
	@Column(name = "auto_expire_date")
	private LocalDateTime autoExpireDate;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_id")
	private EncounterLight encounter;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "orderer")
	private ProviderLight orderer;
	
	@Column(name = "date_stopped")
	private LocalDateTime dateStopped;
	
	@ManyToOne
	@JoinColumn(name = "order_reason")
	private ConceptLight orderReason;
	
	@Column(name = "accession_number")
	private String accessionNumber;
	
	@Column(name = "order_reason_non_coded")
	private String orderReasonNonCoded;
	
	@NotNull
	private String urgency;
	
	@NotBlank
	@Column(name = "order_number")
	private String orderNumber;
	
	@Column(name = "comment_to_fulfiller")
	private String commentToFulfiller;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "care_setting")
	private CareSettingLight careSetting;
	
	@Column(name = "scheduled_date")
	private LocalDateTime scheduledDate;
	
	@Column(name = "sort_weight")
	private Double sortWeight;
	
	@ManyToOne
	@JoinColumn(name = "previous_order_id")
	private OrderLight previousOrder;
	
	@NotNull
	@Column(name = "order_action")
	private String action;
	
	@ManyToOne
	@JoinColumn(name = "order_group_id")
	private OrderGroupLight orderGroup;
	
	@Column(name = "fulfiller_status")
	private String fulfillerStatus;
	
	@Column(name = "fulfiller_comment")
	private String fulfillerComment;
	
	/**
	 * Gets the patient
	 *
	 * @return the patient
	 */
	public PatientLight getPatient() {
		return patient;
	}
	
	/**
	 * Sets the patient
	 *
	 * @param patient the patient to set
	 */
	public void setPatient(PatientLight patient) {
		this.patient = patient;
	}
	
	/**
	 * Gets the orderType
	 *
	 * @return the orderType
	 */
	public OrderTypeLight getOrderType() {
		return orderType;
	}
	
	/**
	 * Sets the orderType
	 *
	 * @param orderType the orderType to set
	 */
	public void setOrderType(OrderTypeLight orderType) {
		this.orderType = orderType;
	}
	
	/**
	 * Gets the concept
	 *
	 * @return the concept
	 */
	public ConceptLight getConcept() {
		return concept;
	}
	
	/**
	 * Sets the concept
	 *
	 * @param concept the concept to set
	 */
	public void setConcept(ConceptLight concept) {
		this.concept = concept;
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
	 * Gets the encounter
	 *
	 * @return the encounter
	 */
	public EncounterLight getEncounter() {
		return encounter;
	}
	
	/**
	 * Sets the encounter
	 *
	 * @param encounter the encounter to set
	 */
	public void setEncounter(EncounterLight encounter) {
		this.encounter = encounter;
	}
	
	/**
	 * Gets the orderer
	 *
	 * @return the orderer
	 */
	public ProviderLight getOrderer() {
		return orderer;
	}
	
	/**
	 * Sets the orderer
	 *
	 * @param orderer the orderer to set
	 */
	public void setOrderer(ProviderLight orderer) {
		this.orderer = orderer;
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
	 * Gets the orderReason
	 *
	 * @return the orderReason
	 */
	public ConceptLight getOrderReason() {
		return orderReason;
	}
	
	/**
	 * Sets the orderReason
	 *
	 * @param orderReason the orderReason to set
	 */
	public void setOrderReason(ConceptLight orderReason) {
		this.orderReason = orderReason;
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
	 * Gets the careSetting
	 *
	 * @return the careSetting
	 */
	public CareSettingLight getCareSetting() {
		return careSetting;
	}
	
	/**
	 * Sets the careSetting
	 *
	 * @param careSetting the careSetting to set
	 */
	public void setCareSetting(CareSettingLight careSetting) {
		this.careSetting = careSetting;
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
	 * Gets the previousOrder
	 *
	 * @return the previousOrder
	 */
	public OrderLight getPreviousOrder() {
		return previousOrder;
	}
	
	/**
	 * Sets the previousOrder
	 *
	 * @param previousOrder the previousOrder to set
	 */
	public void setPreviousOrder(OrderLight previousOrder) {
		this.previousOrder = previousOrder;
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
	 * Gets the orderGroup
	 *
	 * @return the orderGroup
	 */
	public OrderGroupLight getOrderGroup() {
		return orderGroup;
	}
	
	/**
	 * Sets the orderGroup
	 *
	 * @param orderGroup the orderGroup to set
	 */
	public void setOrderGroup(OrderGroupLight orderGroup) {
		this.orderGroup = orderGroup;
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
