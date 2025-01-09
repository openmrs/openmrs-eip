package org.openmrs.eip.fhir.routes.resources.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Concept {
	
	public String uuid;
	
	public String displayString;
	
	public String getUuid() {
		return uuid;
	}
	
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getDisplayString() {
		return displayString;
	}
	
	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}
}
