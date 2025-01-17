package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROCEDURE_ORDER_TYPE_UUID;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Endpoint;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

@UseAdviceWith
class ProcedureRouterTest extends CamelSpringTestSupport {
	
	private static final String PROCEDURE_ORDER_RESPONSE = "{\"uuid\":\"c585bffc-df49-4e57-8a90-6c1d5edb4ffd\",\"orderNumber\":\"ORD-2\",\"accessionNumber\":\"wefewf43r\",\"patient\":{\"uuid\":\"8b562184-bf0f-4ece-8852-780ee315f98e\",\"display\":\"H-3000000 - Siddharth Vaish\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/patient\\/8b562184-bf0f-4ece-8852-780ee315f98e\",\"resourceAlias\":\"patient\"}]},\"concept\":{\"uuid\":\"5a4c17ce-8305-4ea4-baca-0db107ae5ec2\",\"display\":\"Cystoscopie\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/concept\\/5a4c17ce-8305-4ea4-baca-0db107ae5ec2\",\"resourceAlias\":\"concept\"}]},\"action\":\"NEW\",\"careSetting\":{\"uuid\":\"6f0c9a92-6f24-11e3-af88-005056821db0\",\"display\":\"Outpatient\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/caresetting\\/6f0c9a92-6f24-11e3-af88-005056821db0\",\"resourceAlias\":\"caresetting\"}]},\"previousOrder\":null,\"dateActivated\":\"2025-01-08T09:11:53.000+0000\",\"scheduledDate\":null,\"dateStopped\":null,\"autoExpireDate\":null,\"encounter\":{\"uuid\":\"5a286408-167e-4883-aed8-61fb0b0e65ee\",\"display\":\"Orders 01\\/08\\/2025\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/encounter\\/5a286408-167e-4883-aed8-61fb0b0e65ee\",\"resourceAlias\":\"encounter\"}]},\"orderer\":{\"uuid\":\"adbc0d21-c77b-426e-929d-76ff08f9f250\",\"display\":\"admin - Super User\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/provider\\/adbc0d21-c77b-426e-929d-76ff08f9f250\",\"resourceAlias\":\"provider\"}]},\"orderReason\":null,\"orderReasonNonCoded\":null,\"orderType\":{\"uuid\":\"67a92e56-0f88-11ea-8d71-362b9e155667\",\"display\":\"Procedure\",\"name\":\"Procedure\",\"javaClassName\":\"org.openmrs.Order\",\"retired\":false,\"description\":\"An order for Procedure exams\",\"conceptClasses\":[{\"uuid\":\"8d490bf4-c2cc-11de-8d13-0010c6dffd0f\",\"display\":\"Procedure\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/conceptclass\\/8d490bf4-c2cc-11de-8d13-0010c6dffd0f\",\"resourceAlias\":\"conceptclass\"}]}],\"parent\":null,\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/ordertype\\/67a92e56-0f88-11ea-8d71-362b9e155667\",\"resourceAlias\":\"ordertype\"},{\"rel\":\"full\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/ordertype\\/67a92e56-0f88-11ea-8d71-362b9e155667?v=full\",\"resourceAlias\":\"ordertype\"}],\"resourceVersion\":\"1.10\"},\"urgency\":\"ROUTINE\",\"instructions\":null,\"commentToFulfiller\":null,\"display\":\"Cystoscopie\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/order\\/c585bffc-df49-4e57-8a90-6c1d5edb4ffd\",\"resourceAlias\":\"order\"},{\"rel\":\"full\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/order\\/c585bffc-df49-4e57-8a90-6c1d5edb4ffd?v=full\",\"resourceAlias\":\"order\"}],\"type\":\"order\",\"resourceVersion\":\"1.10\"}";
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() {
		RouteBuilder rb = new ProcedureRouter();
		rb.from(FhirResource.PROCEDURE.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@Override
	public boolean isUseDebugger() {
		return true;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-procedure-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				context.getPropertiesComponent().setLocation("classpath:application-test.properties");
				weaveByToUri("http:*").replace().to("mock:http");
				weaveByToUri(
				    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid ='${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-order-type");
				weaveByToUri(
				    "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-orders");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.PROCEDURE.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
	}
	
	@Test
	void shouldHandleProcedureOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint http = getMockEndpoint("mock:http");
		http.expectedMessageCount(1);
		http.whenAnyExchangeReceived((exchange) -> {
			Message httpOutput = exchange.getMessage();
			httpOutput.setBody(PROCEDURE_ORDER_RESPONSE);
		});
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", PROCEDURE_ORDER_TYPE_UUID);
			sqlOutput.setBody(Collections.singletonList(map));
		});
		
		MockEndpoint sqlOrders = getMockEndpoint("mock:sql-orders");
		sqlOrders.expectedMessageCount(1);
		sqlOrders.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> output = new HashMap<>();
			output.put("voided", String.valueOf(0));
			output.put("order_action", "NEW");
			output.put("previous_order_id", String.valueOf(1));
			sqlOutput.setBody(Collections.singletonList(output));
		});
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("orders");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a service request object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(ServiceRequest.class));
		
		http.assertIsSatisfied();
		sqlOrderType.assertIsSatisfied();
		sqlOrders.assertIsSatisfied();
	}
	
	@Test
	void shouldSkipUnknownEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(0);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("unknown_table");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		fhir.assertIsSatisfied();
	}
}
