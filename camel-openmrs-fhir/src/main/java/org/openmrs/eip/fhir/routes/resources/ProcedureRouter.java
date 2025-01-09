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
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.StringType;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.fhir.routes.resources.models.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProcedureRouter extends BaseFhirResourceRouter {
	
	ProcedureRouter() {
		super(FhirResource.PROCEDURE);
	}
	
	@Override
	public void configure() throws Exception {
		from(FhirResource.PROCEDURE.incomingUrl()).routeId("fhir-procedure-router").filter(isSupportedTable()).toD(
		    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + PROCEDURE_ORDER_TYPE_UUID + "'"))
		        .log(LoggingLevel.INFO,
		            "Processing ProcedureRouter ${exchangeProperty.event.tableName} message uuid ${body[0]['uuid']}")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).when(simple("${body[0]['order_action']} == 'DISCONTINUE'"))
		        .toD(
		            "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
		        .toD("fhir:read/resourceById?resourceClass=ServiceRequest&stringId=${body[0]['uuid']}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).to(FhirResource.PROCEDURE.outgoingUrl()).otherwise()
		        .setHeader("Authorization", constant("Basic YWRtaW46QWRtaW4xMjM="))
		        .setHeader("CamelHttpMethod", constant("GET"))
		        .log(LoggingLevel.INFO, "ProcedureRouter order uuid: ${exchangeProperty.event.identifier}")
		        .toD("http://openmrs:8080/openmrs/ws/rest/v1/order/${exchangeProperty.event.identifier}")
		        .process(exchange -> {
			        log.info("Response in ProcedureRouter: {}", exchange.getMessage().getBody(String.class));
			        ObjectMapper objectMapper = new ObjectMapper();
			        Order order = objectMapper.readValue(exchange.getMessage().getBody(String.class), Order.class);
			        log.info("Order in ProcedureRouter: {}", order);
			        
			        Bundle bundle = new Bundle();
			        Patient patient = new Patient();
			        patient.setId(order.getPatient().getUuid());
			        patient.setActive(true);
			        patient.setName(Collections.singletonList(new HumanName().setFamily(order.getPatient().getDisplay())
			                .setGiven(Collections.singletonList(new StringType(order.getPatient().getDisplay())))));
			        patient.setIdentifier(Collections.singletonList(new Identifier()
			                .setUse(Identifier.IdentifierUse.OFFICIAL).setValue(order.getPatient().getDisplay())));
			        patient.setBirthDate(new Date());
			        bundle.addEntry().setResource(patient);
			        
			        Encounter encounter = new Encounter();
			        encounter.setId(order.getEncounter().getUuid());
			        encounter.setPartOf(new Reference().setReference("Encounter/" + order.getEncounter().getUuid()));
			        bundle.addEntry().setResource(encounter);
			        
			        ServiceRequest serviceRequest = new ServiceRequest();
			        serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
			        serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
			        serviceRequest.setCode(new CodeableConcept(new Coding().setCode(order.getConcept().getUuid())
			                .setDisplay(order.getConcept().getDisplayString()))
			                        .setText(order.getConcept().getDisplayString()));
			        serviceRequest.setSubject(new Reference().setReference("Patient/" + order.getPatient().getUuid())
			                .setType("Patient").setDisplay(order.getPatient().getDisplay()));
			        serviceRequest.setEncounter(
			            new Reference().setReference("Encounter/" + order.getEncounter().getUuid()).setType("Encounter"));
			        serviceRequest.setRequester(new Reference().setReference("Practitioner/" + order.getOrderer().getUuid())
			                .setType("Practitioner").setDisplay(order.getOrderer().getDisplay()));
			        
			        bundle.addEntry().setResource(serviceRequest);
			        
			        exchange.getMessage().setBody(bundle);
			        
		        }).log(LoggingLevel.INFO, "Response in ProcedureRouter: ${body}")
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
}
