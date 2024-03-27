package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class ObservationFhirRouter extends BaseFhirResourceRouter {
	
	ObservationFhirRouter() {
		super(FhirResource.OBSERVATION);
	}
	
	@Override
	public void configure() {
		from(FhirResource.OBSERVATION.incomingUrl()).routeId("fhir-observation-router").filter(isSupportedTable())
				.log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        .toD("fhir:read/resourceById?resourceClass=Observation&stringId=${exchangeProperty.event.identifier}")
		        .marshal(DEFAULT_FORMAT).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.OBSERVATION.outgoingUrl());
	}
}
