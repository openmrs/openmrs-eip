package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import java.util.Date;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Setter;

@Setter
@Component
public class ProcedureRouter extends BaseFhirResourceRouter {
	
	@Value("${openmrs.baseUrl}")
	private String openmrsBaseUrl;
	
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
		        .to(FhirResource.PROCEDURE.outgoingUrl()).when(simple("${body[0]['order_action']} == 'DISCONTINUE'"))
		        .toD(
		            "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
		        .setHeader("CamelHttpMethod", constant("GET")).toD(openmrsBaseUrl + "/ws/rest/v1/order/${body[0]['uuid']}")
		        .unmarshal().json(JsonLibrary.Jackson, Order.class).process(exchange -> {
			        Order order = exchange.getIn().getBody(Order.class);
			        exchange.getMessage().setBody(mapOrderToServiceRequest(order));
		        }).setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).to(FhirResource.PROCEDURE.outgoingUrl()).otherwise()
		        .setHeader("CamelHttpMethod", constant("GET"))
		        .toD(openmrsBaseUrl + "/ws/rest/v1/order/${exchangeProperty.event.identifier}").unmarshal()
		        .json(JsonLibrary.Jackson, Order.class).process(exchange -> {
			        Order order = exchange.getIn().getBody(Order.class);
			        exchange.getMessage().setBody(mapOrderToServiceRequest(order));
		        }).setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
	
	private ServiceRequest mapOrderToServiceRequest(Order order) {
		ServiceRequest serviceRequest = new ServiceRequest();
		serviceRequest.setStatus(determineServiceRequestStatus(order));
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
	
	private ServiceRequest.ServiceRequestStatus determineServiceRequestStatus(Order order) {
		Date currentDate = new Date();
		boolean isCompeted = order.isActivated()
		        && ((order.getDateStopped() != null && currentDate.after(order.getDateStopped()))
		                || (order.getAutoExpireDate() != null && currentDate.after(order.getAutoExpireDate())));
		boolean isDiscontinued = order.isActivated() && order.getAction().equals("DISCONTINUE");
		
		if ((isCompeted && isDiscontinued)) {
			return ServiceRequest.ServiceRequestStatus.UNKNOWN;
		} else if (isDiscontinued) {
			return ServiceRequest.ServiceRequestStatus.REVOKED;
		} else if (isCompeted) {
			return ServiceRequest.ServiceRequestStatus.COMPLETED;
		} else {
			return ServiceRequest.ServiceRequestStatus.ACTIVE;
		}
	}
}
