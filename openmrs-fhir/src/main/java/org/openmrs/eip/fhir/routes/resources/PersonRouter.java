package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class PersonRouter extends BaseFhirResourceRouter {
	
	public PersonRouter() {
		super(FhirResource.PERSON);
	}
	
	@Override
	public void configure() {
		from(FhirResource.PERSON.incomingUrl()).routeId("fhir-person-router").filter(isSupportedTable())
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
				.choice().when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").in("patient", "person"))
					.toD(
						"sql:SELECT voided FROM person WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				.otherwise()
					.toD("sql:SELECT voided FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				.endChoice()
				.choice()
					.when(simple("${exchangeProperty.operation} == 'd' || ${body[0]} == 1"))
					.setHeader(HEADER_FHIR_EVENT_TYPE, constant("d"))
					.setBody(simple("${exchangeProperty.event.identifier}"))
					.to(FhirResource.PERSON.outgoingUrl())
				.otherwise()
					// person or patient are basically the top-level object
					.choice().when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").isEqualTo("person"))
					.toD("fhir:read/resourceById?resourceClass=Person&stringId=${exchangeProperty.event.identifier}").otherwise()
					.toD(
						"sql:SELECT uuid FROM person WHERE person_id = (SELECT t.person FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
					.toD("fhir:read/resourceById?resourceClass=Patient&stringId=${body[0].get('uuid')}").end()
					.setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
					.to(FhirResource.PERSON.outgoingUrl())
				.endChoice();
	}
}
