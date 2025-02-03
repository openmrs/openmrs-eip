package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.DRUG_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Component
@Setter
public class MedicationRequestRouter extends BaseFhirResourceRouter {
	
	@Value("${eip.drug.order.type.uuid:" + DRUG_ORDER_TYPE_UUID + "}")
	private String drugOrderTypeUuid;
	
	MedicationRequestRouter() {
		super(FhirResource.MEDICATIONREQUEST);
	}
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.MEDICATIONREQUEST.incomingUrl()).routeId("fhir-medicationrequest-router").filter(
		    isSupportedTable()).toD(
		        "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + drugOrderTypeUuid + "'"))
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.MEDICATIONREQUEST.outgoingUrl()).when(simple("${body[0]['order_action']} == 'DISCONTINUE'"))
		        .toD(
		            "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
		        .toD("fhir:read/resourceById?resourceClass=MedicationRequest&stringId=${body[0]['uuid']}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).to(FhirResource.MEDICATIONREQUEST.outgoingUrl())
		        .otherwise()
		        .toD("fhir:read/resourceById?resourceClass=MedicationRequest&stringId=${exchangeProperty.event.identifier}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.MEDICATIONREQUEST.outgoingUrl()).endChoice().end();
		
	}
}
