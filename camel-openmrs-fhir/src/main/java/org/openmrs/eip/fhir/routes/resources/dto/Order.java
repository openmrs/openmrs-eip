package org.openmrs.eip.fhir.routes.resources.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
	
	public float quantity;
	
	public QuantityUnits quantityUnits;
}
