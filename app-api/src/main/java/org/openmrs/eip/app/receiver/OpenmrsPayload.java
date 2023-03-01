package org.openmrs.eip.app.receiver;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;

@JsonInclude(Include.NON_NULL)
@Getter
public class OpenmrsPayload {
	
	String resource;
	
	String subResource;
	
	String uuid;
	
	public OpenmrsPayload(String resource, String subResource, String uuid) {
		this.resource = resource;
		this.subResource = subResource;
		this.uuid = uuid;
	}
	
}
