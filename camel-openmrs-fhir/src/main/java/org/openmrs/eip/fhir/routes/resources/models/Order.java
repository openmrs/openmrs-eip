package org.openmrs.eip.fhir.routes.resources.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
	
	public String uuid;
	
	public String orderNumber;
	
	public String accessionNumber;
	
	public Patient patient;
	
	public Concept concept;
	
	public String action;
	
	public Object previousOrder;
	
	public String dateActivated;
	
	public Object scheduledDate;
	
	public Object dateStopped;
	
	public Object autoExpireDate;
	
	public Encounter encounter;
	
	public Orderer orderer;
	
	public Object orderReason;
	
	public Object orderReasonNonCoded;
	
	public String urgency;
	
	public Object instructions;
	
	public Object commentToFulfiller;
	
	public String display;
	
	public String type;
	
	public String resourceVersion;
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getOrderNumber() {
		return orderNumber;
	}
	
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	
	public String getAccessionNumber() {
		return accessionNumber;
	}
	
	public void setAccessionNumber(String accessionNumber) {
		this.accessionNumber = accessionNumber;
	}
	
	public Patient getPatient() {
		return patient;
	}
	
	public void setPatient(Patient patient) {
		this.patient = patient;
	}
	
	public Concept getConcept() {
		return concept;
	}
	
	public void setConcept(Concept concept) {
		this.concept = concept;
	}
	
	public String getAction() {
		return action;
	}
	
	public void setAction(String action) {
		this.action = action;
	}
	
	public Object getPreviousOrder() {
		return previousOrder;
	}
	
	public void setPreviousOrder(Object previousOrder) {
		this.previousOrder = previousOrder;
	}
	
	public String getDateActivated() {
		return dateActivated;
	}
	
	public void setDateActivated(String dateActivated) {
		this.dateActivated = dateActivated;
	}
	
	public Object getScheduledDate() {
		return scheduledDate;
	}
	
	public void setScheduledDate(Object scheduledDate) {
		this.scheduledDate = scheduledDate;
	}
	
	public Object getDateStopped() {
		return dateStopped;
	}
	
	public void setDateStopped(Object dateStopped) {
		this.dateStopped = dateStopped;
	}
	
	public Object getAutoExpireDate() {
		return autoExpireDate;
	}
	
	public void setAutoExpireDate(Object autoExpireDate) {
		this.autoExpireDate = autoExpireDate;
	}
	
	public Encounter getEncounter() {
		return encounter;
	}
	
	public void setEncounter(Encounter encounter) {
		this.encounter = encounter;
	}
	
	public Orderer getOrderer() {
		return orderer;
	}
	
	public void setOrderer(Orderer orderer) {
		this.orderer = orderer;
	}
	
	public Object getOrderReason() {
		return orderReason;
	}
	
	public void setOrderReason(Object orderReason) {
		this.orderReason = orderReason;
	}
	
	public Object getOrderReasonNonCoded() {
		return orderReasonNonCoded;
	}
	
	public void setOrderReasonNonCoded(Object orderReasonNonCoded) {
		this.orderReasonNonCoded = orderReasonNonCoded;
	}
	
	public String getUrgency() {
		return urgency;
	}
	
	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}
	
	public Object getInstructions() {
		return instructions;
	}
	
	public void setInstructions(Object instructions) {
		this.instructions = instructions;
	}
	
	public Object getCommentToFulfiller() {
		return commentToFulfiller;
	}
	
	public void setCommentToFulfiller(Object commentToFulfiller) {
		this.commentToFulfiller = commentToFulfiller;
	}
	
	public String getDisplay() {
		return display;
	}
	
	public void setDisplay(String display) {
		this.display = display;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getResourceVersion() {
		return resourceVersion;
	}
	
	public void setResourceVersion(String resourceVersion) {
		this.resourceVersion = resourceVersion;
	}
}
