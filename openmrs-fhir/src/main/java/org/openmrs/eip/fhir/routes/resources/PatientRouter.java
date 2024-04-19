package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.stereotype.Component;

/**
 * Router for Patient events. Patient data can be updated from multiple sub-tables. When sub-table
 * updates happen, we need to link back the Patient entity. Additionally, the patient record is
 * basically a sub-record of a person. However, the logic for this is already handled by the OpenMRS
 * Watcher
 */
@Component
public class PatientRouter extends BaseFhirResourceRouter {
	
	private static final String PATIENT_IDENTIFIER = "patient_identifier";
	
	PatientRouter() {
		super(FhirResource.PATIENT);
	}
	
	@Override
	public void configure() {
		from(FhirResource.PATIENT.incomingUrl()).routeId("fhir-patient-router").filter(isSupportedTable())
		        .log(LoggingLevel.INFO, "Processing ${exchangeProperty.event.tableName} message").choice()
		        .when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").in("patient", "person"))
		        .toD(
		            "sql:SELECT voided FROM person WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").isEqualTo(PATIENT_IDENTIFIER))
		        .toD(
		            "sql:SELECT voided FROM person WHERE person_id = (SELECT t.patient_id FROM patient_identifier t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
		        .otherwise()
		        .toD(
		            "sql:SELECT voided FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
		        .end().choice().when(simple("${exchangeProperty.operation} == 'd' || ${body[0]} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.PATIENT.outgoingUrl()).otherwise()
		        // person or patient are basically the top-level object
		        .choice().when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").in("patient", "person"))
		        .toD("fhir:read/resourceById?resourceClass=Patient&stringId=${exchangeProperty.event.identifier}")
		        .otherwise().choice()
		        .when(simple("${exchangeProperty." + PROP_EVENT_TABLE_NAME + "}").isEqualTo(PATIENT_IDENTIFIER))
		        .toD(
		            "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.patient_id FROM patient_identifier t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
		        // person_name or person_address
		        .otherwise()
		        .toD(
		            "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
		        .end().toD("fhir:read/resourceById?resourceClass=Patient&stringId=${body[0].get('uuid')}").end().end()
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PATIENT.outgoingUrl()).endChoice().end();
	}
}
