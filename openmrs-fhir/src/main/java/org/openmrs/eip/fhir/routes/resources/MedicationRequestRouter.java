package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.hl7.fhir.r4.model.MedicationRequest;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class MedicationRequestRouter extends BaseFhirResourceRouter {
	
	MedicationRequestRouter() {
		super(FhirResource.MEDICATIONREQUEST);
	}
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.MEDICATIONREQUEST.incomingUrl()).routeId("fhir-medicationrequest-router")
		        .filter(isSupportedTable())
				.toD("sql: SELECT uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid = ${exchangeProperty.event.identifier}?dataSource=#openmrsDataSource")
				.filter(simple("${body[0]} == '131168f4-15f5-102d-96e4-000c29c2a5d7'"))
				.log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
				.toD(
						"sql:SELECT voided FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				.choice().when(simple("${exchangeProperty.operation} == 'd' || ${body[0]} == 1"))
					.setHeader(HEADER_FHIR_EVENT_TYPE, constant("d"))
					.setBody(simple("${exchangeProperty.event.identifier}"))
					.to(FhirResource.MEDICATIONREQUEST.outgoingUrl())
				.otherwise()
					.toD("fhir:read/resourceById?resourceClass=MedicationRequest&stringId=${exchangeProperty.event.identifier}")
					.setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
					.to(FhirResource.MEDICATIONREQUEST.outgoingUrl())
				.endChoice();
		
	}
}
