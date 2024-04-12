package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestRouter extends BaseFhirResourceRouter {
	
	ServiceRequestRouter() {
		super(FhirResource.SERVICEREQUEST);
	}
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.SERVICEREQUEST.incomingUrl()).routeId("fhir-servicerequest-router").filter(isSupportedTable())
				.toD("sql: SELECT uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid = ${exchangeProperty.event.identifier}?dataSource=#openmrsDataSource")
				.filter(simple("${body[0]} == '52a447d3-a64a-11e3-9aeb-50e549534c5e'"))
				.log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
				.toD(
						"sql:SELECT voided FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				.choice()
					.when(simple("${exchangeProperty.operation} == 'd' || ${body[0]} == 1"))
						.setHeader(HEADER_FHIR_EVENT_TYPE, constant("d"))
						.setBody(simple("${exchangeProperty.event.identifier}"))
						.to(FhirResource.SERVICEREQUEST.outgoingUrl())
				.otherwise()
					.toD("fhir:read/resourceById?resourceClass=ServiceRequest&stringId=${exchangeProperty.event.identifier}")
					.setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
					.to(FhirResource.SERVICEREQUEST.outgoingUrl())
				.endChoice();
	}
}
