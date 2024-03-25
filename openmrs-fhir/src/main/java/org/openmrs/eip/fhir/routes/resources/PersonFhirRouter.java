package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class PersonFhirRouter extends BaseFhirResourceRouter {
	
	public PersonFhirRouter() {
		super(FhirResource.PERSON);
	}
	
	@Override
	public void configure() {
		from(FhirResource.PERSON.incomingUrl()).routeId("fhir-person-router").filter(isSupportedTable())
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        // person or patient are basically the top-level object
		        .choice().when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").isEqualTo("person"))
		        .toD("fhir:read/resourceById?resourceClass=Person&stringId=${exchangeProperty.event.identifier}").otherwise()
		        .toD(
		            "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.person FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = :#${exchangeProperty.event.identifier})?dataSource=#openmrsDataSource")
		        .toD("fhir:read/resourceById?resourceClass=Patient&stringId=${body[0].get('uuid')}").end()
		        .marshal(DEFAULT_FORMAT).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PERSON.outgoingUrl());
	}
}
