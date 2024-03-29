package org.openmrs.eip.fhir.components;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.support.DefaultEndpoint;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
@UriEndpoint(firstVersion = "1.0.0", scheme = "openmrs-fhir", title = "OpenMRS FHIR Bundle", syntax = "openmrs-fhir:")
public class FhirSearchBundleEndpoint extends DefaultEndpoint {
	
	@UriParam(description = "The FHIR resource type to fetch")
	private String resource;
	
	@UriParam(description = "The id of the resource to find included components from")
	private String id;
	
	@UriParam(description = "The types of resources to include via the _include parameter (refers to resources referenced within the containing resource, e.g. Task:basedOn when requesting a Task)")
	private String include = "";
	
	@UriParam(description = "The types of resources to include via the _revinclude parameter (refers to resources that refer to this resource, e.g., Encounter:subject when requesting a patient)")
	private String revinclude = "";
	
	protected FhirSearchBundleEndpoint(String endpointUri, Component component) {
		super(endpointUri, component);
	}
	
	@Override
	public Producer createProducer() {
		return new FhirSearchBundleProducer(this);
	}
	
	@Override
	public Consumer createConsumer(Processor processor) {
		throw new UnsupportedOperationException("You cannot receive messages from this endpoint: " + getEndpointUri());
	}
}
