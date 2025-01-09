package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import java.util.Collections;
import java.util.Date;

import org.apache.camel.LoggingLevel;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Narrative;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.fhir.routes.resources.models.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProcedureRouter extends BaseFhirResourceRouter {
	
	ProcedureRouter() {
		super(FhirResource.PROCEDURE);
	}
	
	@Autowired
	private ProcedureProcessor procedureProcessor;
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.PROCEDURE.incomingUrl()).routeId("fhir-procedure-router").filter(isSupportedTable()).toD(
		    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + PROCEDURE_ORDER_TYPE_UUID + "'"))
		        .log(LoggingLevel.INFO, "Processing ProcedureRouter ${exchangeProperty.event.tableName}")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .log(LoggingLevel.INFO, "ProcedureRouter event body uuid ${body[0]['uuid']}").choice()
		        .when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl())
		        //		        .when(simple("${body[0]['order_action']} == 'DISCONTINUE'"))
		        //		        .toD(
		        //		            "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
		        //		        .setHeader("Authorization", constant("Basic YWRtaW46QWRtaW4xMjM="))
		        //		        .setHeader("CamelHttpMethod", constant("GET"))
		        //		        .log(LoggingLevel.INFO, "DISCONTINUE ProcedureRouter order uuid: ${exchangeProperty.event.identifier} and ${body[0]['uuid']}")
		        //		        .toD("http://openmrs:8080/openmrs/ws/rest/v1/order/${body[0]['uuid']}").process(procedureProcessor)
		        //		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).to(FhirResource.PROCEDURE.outgoingUrl())
		        .otherwise().setHeader("Authorization", constant("Basic YWRtaW46QWRtaW4xMjM="))
		        .setHeader("CamelHttpMethod", constant("GET"))
		        .toD("http://openmrs:8080/openmrs/ws/rest/v1/order/${exchangeProperty.event.identifier}")
		        .process(procedureProcessor)
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
}
