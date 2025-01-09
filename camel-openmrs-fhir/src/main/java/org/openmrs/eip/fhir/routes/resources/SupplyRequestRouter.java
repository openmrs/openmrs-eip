package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.SUPPLY_REQUEST_ORDER_TYPE_UUID;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SupplyRequestRouter extends BaseFhirResourceRouter {
	
	SupplyRequestRouter() {
		super(FhirResource.SUPPLYREQUEST);
	}
	
	@Autowired
	private SupplyRequestProcessor supplyRequestProcessor;
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.SUPPLYREQUEST.incomingUrl()).routeId("fhir-procedure-router").filter(isSupportedTable()).toD(
		    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + SUPPLY_REQUEST_ORDER_TYPE_UUID + "'"))
		        .log(LoggingLevel.INFO, "Processing SupplyRequestRouter ${exchangeProperty.event.tableName}")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .log(LoggingLevel.INFO, "ProcedureRouter event body uuid ${body[0]['uuid']}").choice()
		        .when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.SUPPLYREQUEST.outgoingUrl()).otherwise()
		        .setHeader("Authorization", constant("Basic YWRtaW46QWRtaW4xMjM="))
		        .setHeader("CamelHttpMethod", constant("GET"))
		        .toD("http://openmrs:8080/openmrs/ws/rest/v1/order/${exchangeProperty.event.identifier}")
		        .process(supplyRequestProcessor)
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
}
