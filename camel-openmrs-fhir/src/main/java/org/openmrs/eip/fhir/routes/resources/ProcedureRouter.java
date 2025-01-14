package org.openmrs.eip.fhir.routes.resources;

import static java.util.Base64.getEncoder;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;

import org.apache.camel.LoggingLevel;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		        .choice().when(simple("${exchangeProperty.event.operation} == 'd' || ${body[0]['voided']} == 1"))
		        .setHeader(HEADER_FHIR_EVENT_TYPE, constant("d")).setBody(simple("${exchangeProperty.event.identifier}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).otherwise().process(exchange -> {
			        String username = exchange.getContext().resolvePropertyPlaceholders("{{openmrs.username}}");
			        String password = exchange.getContext().resolvePropertyPlaceholders("{{openmrs.password}}");
			        String auth = username + ":" + password;
			        String base64Auth = getEncoder().encodeToString(auth.getBytes());
			        exchange.getIn().setHeader("Authorization", "Basic " + base64Auth);
		        }).setHeader("CamelHttpMethod", constant("GET"))
		        .toD("{{openmrs.baseUrl}}/ws/rest/v1/order/${exchangeProperty.event.identifier}").process(procedureProcessor)
		        .setHeader(HEADER_FHIR_EVENT_TYPE, simple("${exchangeProperty." + PROP_EVENT_OPERATION + "}"))
		        .to(FhirResource.PROCEDURE.outgoingUrl()).endChoice().end();
	}
}
