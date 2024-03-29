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
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        .toD("fhir:read/resourceById?resourceClass=ServiceRequest&stringId=${exchangeProperty.event.identifier}")
		        // since we watch the orders table, we need to filter out orders that are not service requests
		        .filter((exchange) -> {
			        Object messageBody = exchange.getMessage().getBody();
			        return messageBody instanceof ServiceRequest;
		        }).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.SERVICEREQUEST.outgoingUrl());
	}
}
