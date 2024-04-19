package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class EncounterRouter extends BaseFhirResourceRouter {
	
	EncounterRouter() {
		super(FhirResource.ENCOUNTER);
	}
	
	@Override
	public void configure() {
		from(FhirResource.ENCOUNTER.incomingUrl()).routeId("fhir-encounter-router").filter(isSupportedTable()).log(
		    LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message").toD(
		        "sql:SELECT voided FROM ${exchangeProperty.event.tableName} WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .choice().when(simple("${exchangeProperty.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.ENCOUNTER.outgoingUrl()).otherwise()
		        .toD("fhir:read/resourceById?resourceClass=Encounter&stringId=${exchangeProperty.event.identifier}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.ENCOUNTER.outgoingUrl()).endChoice().end();
	}
}
