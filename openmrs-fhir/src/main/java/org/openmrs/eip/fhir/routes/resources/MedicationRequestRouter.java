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
		        .filter(isSupportedTable()).log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message")
		        .toD("fhir:read/resourceById?resourceClass=MedicationRequest&stringId=${exchangeProperty.event.identifier}")
		        // since we watch the orders table, we need to filter out orders that are not medication requests
		        .filter((exchange) -> {
			        Object messageBody = exchange.getMessage().getBody();
			        return messageBody instanceof MedicationRequest;
		        }).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.MEDICATIONREQUEST.outgoingUrl());
		
	}
}
