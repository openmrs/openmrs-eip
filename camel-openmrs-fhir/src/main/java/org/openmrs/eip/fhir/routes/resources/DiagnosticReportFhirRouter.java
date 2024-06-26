package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

@Component
public class DiagnosticReportFhirRouter extends BaseFhirResourceRouter {
	
	DiagnosticReportFhirRouter() {
		super(FhirResource.DIAGNOSTICREPORT);
	}
	
	@Override
	public void configure() {
		from(FhirResource.DIAGNOSTICREPORT.incomingUrl()).routeId("fhir-diagnosticreport-router").filter(
		    isSupportedTable()).log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message").toD(
		        "sql:SELECT voided FROM obs WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource") // TODO: Query correct table
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.DIAGNOSTICREPORT.outgoingUrl()).otherwise()
		        .toD("fhir:read/resourceById?resourceClass=DiagnosticReport&stringId=${exchangeProperty.event.identifier}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.DIAGNOSTICREPORT.outgoingUrl()).endChoice().end();
	}
}
