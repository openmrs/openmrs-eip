package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestRouter extends BaseFhirResourceRouter {
	
	@Value("${eip.test.order.concept.uuid}")
	private String testOrderTypeUuid;
	
	@Value("${eip.imaging.order.concept.uuid}")
	private String imagingOrderTypeUuid;
	
	ServiceRequestRouter() {
		super(FhirResource.SERVICEREQUEST);
	}
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.SERVICEREQUEST.incomingUrl()).routeId("fhir-servicerequest-router").filter(isSupportedTable()).toD(
		    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + testOrderTypeUuid + "' || ${body[0]['uuid']} == '"
		                + imagingOrderTypeUuid + "'"))
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.SERVICEREQUEST.outgoingUrl()).when(simple("${body[0]['order_action']} == 'DISCONTINUE'"))
		        .toD(
		            "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
		        .toD("fhir:read/resourceById?resourceClass=ServiceRequest&stringId=${body[0]['uuid']}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).to(FhirResource.SERVICEREQUEST.outgoingUrl()).otherwise()
		        .toD("fhir:read/resourceById?resourceClass=ServiceRequest&stringId=${exchangeProperty.event.identifier}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.SERVICEREQUEST.outgoingUrl()).endChoice().end();
	}
}
