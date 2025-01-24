package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.http.HttpComponent;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.fhir.routes.resources.dto.Order;
import org.openmrs.eip.fhir.spring.OpenmrsRestConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProcedureRouter extends BaseFhirResourceRouter {
	
	@Autowired
	private OpenmrsRestConfiguration openmrsRestConfiguration;
	
	ProcedureRouter() {
		super(FhirResource.PROCEDURE);
	}
	
	@Override
	public void configure() throws Exception {
		getCamelContext().getComponent("http", HttpComponent.class)
		        .setHttpClientConfigurer(openmrsRestConfiguration.createHttpClientConfigurer());
		
		from(FhirResource.PROCEDURE.incomingUrl()).routeId("fhir-procedure-router").filter(isSupportedTable()).toD(
		    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .filter(simple("${body[0]['uuid']} == '" + PROCEDURE_ORDER_TYPE_UUID + "'"))
		        .log(LoggingLevel.INFO, "Processing ProcedureRouter ${exchangeProperty.event.tableName}")
		        .toD(
		            "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).otherwise().setHeader("CamelHttpMethod", constant("GET"))
		        .toD("{{openmrs.baseUrl}}/ws/rest/v1/order/${exchangeProperty.event.identifier}").unmarshal()
		        .json(JsonLibrary.Jackson, Order.class).process(exchange -> {
			        Order order = exchange.getIn().getBody(Order.class);
			        exchange.getMessage().setBody(mapOrderToServiceRequest(order));
		        }).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
	
	private ServiceRequest mapOrderToServiceRequest(Order order) {
		ServiceRequest serviceRequest = new ServiceRequest();
		if (order.getAction().equals("DISCONTINUE")) {
			serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.COMPLETED);
		} else {
			serviceRequest.setStatus(ServiceRequest.ServiceRequestStatus.ACTIVE);
		}
		serviceRequest.setIntent(ServiceRequest.ServiceRequestIntent.ORDER);
		serviceRequest.setCode(new CodeableConcept(
		        new Coding().setCode(order.getConcept().getUuid()).setDisplay(order.getConcept().getDisplay()))
		                .setText(order.getConcept().getDisplay()));
		serviceRequest.setSubject(new Reference().setReference("Patient/" + order.getPatient().getUuid()).setType("Patient")
		        .setDisplay(order.getPatient().getDisplay()));
		serviceRequest.setEncounter(
		    new Reference().setReference("Encounter/" + order.getEncounter().getUuid()).setType("Encounter"));
		serviceRequest.setRequester(new Reference().setReference("Practitioner/" + order.getOrderer().getUuid())
		        .setType("Practitioner").setDisplay(order.getOrderer().getDisplay()));
		
		return serviceRequest;
	}
}
