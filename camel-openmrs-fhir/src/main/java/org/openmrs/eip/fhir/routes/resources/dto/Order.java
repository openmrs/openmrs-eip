package org.openmrs.eip.fhir.routes.resources.dto;

import java.sql.Timestamp;
import java.util.Date;

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
	
	public Date dateActivated;
	
	public Object scheduledDate;
	
	public Date dateStopped;
	
	public Date autoExpireDate;
	
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
	
	public boolean isActivated() {
		return this.isActivated(new Date());
	}
	
	public boolean isActivated(Date checkDate) {
		if (this.dateActivated == null) {
			return false;
		} else {
			if (checkDate == null) {
				checkDate = new Date();
			}
			
			return compare(this.dateActivated, checkDate) <= 0;
		}
	}
	
	public static int compare(Date d1, Date d2) {
		if (d1 instanceof Timestamp && d2 instanceof Timestamp) {
			return d1.compareTo(d2);
		} else {
			if (d1 instanceof Timestamp) {
				d1 = new Date(d1.getTime());
			}
			
			if (d2 instanceof Timestamp) {
				d2 = new Date(d2.getTime());
			}
			
			return d1.compareTo(d2);
		}
	}
}
