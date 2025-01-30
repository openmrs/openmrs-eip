package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.DRUG_ORDER_TYPE_UUID;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.TEST_ORDER_TYPE_UUID;

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
import org.hl7.fhir.r4.model.MedicationRequest;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

@UseAdviceWith
class MedicationRequestRouterTest extends CamelSpringTestSupport {
	
	private static final String DRUG_ORDER_UUID = "f32e93f8-5cb0-457e-b98e-9d12dd1f3ed9";
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() {
		MedicationRequestRouter medicationRequestRouter = new MedicationRequestRouter();
		medicationRequestRouter.setDrugOrderTypeUuid(DRUG_ORDER_TYPE_UUID);
		medicationRequestRouter.from(FhirResource.MEDICATIONREQUEST.outgoingUrl()).to("mock:result");
		return medicationRequestRouter;
	}
	
	@Override
	public boolean isUseDebugger() {
		return true;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-medicationrequest-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
				weaveByToUri(
				    "sql:SELECT ot.uuid as uuid from order_type ot join orders o on o.order_type_id = ot.order_type_id where o.uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-order-type");
				weaveByToUri(
				    "sql:SELECT voided, order_action, previous_order_id FROM orders WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-orders");
				weaveByToUri(
				    "sql:SELECT uuid FROM orders WHERE order_id = ${body[0]['previous_order_id']}?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-previous-order");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.MEDICATIONREQUEST.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
	}
	
	@Test
	void shouldHandleDrugOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", DRUG_ORDER_TYPE_UUID);
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
		
		MockEndpoint sqlPreviousOrder = getMockEndpoint("mock:sql-previous-order");
		sqlPreviousOrder.expectedMessageCount(0);
		sqlPreviousOrder.setResultWaitTime(100);
		sqlPreviousOrder.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", DRUG_ORDER_UUID)));
		});
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			MedicationRequest medicationRequest = new MedicationRequest();
			medicationRequest.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(medicationRequest);
		});
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("drug_order");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a medication request object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(MedicationRequest.class));
		
		fhir.assertIsSatisfied();
		sqlOrderType.assertIsSatisfied();
		sqlOrders.assertIsSatisfied();
		sqlPreviousOrder.assertIsSatisfied();
	}
	
	@Test
	void shouldHandleOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			MedicationRequest medicationRequest = new MedicationRequest();
			medicationRequest.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(medicationRequest);
		});
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", DRUG_ORDER_TYPE_UUID);
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
		
		MockEndpoint sqlPreviousOrder = getMockEndpoint("mock:sql-previous-order");
		sqlPreviousOrder.expectedMessageCount(0);
		sqlPreviousOrder.setResultWaitTime(100);
		sqlPreviousOrder.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", DRUG_ORDER_UUID)));
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
		
		// Verify we got a medication request object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(MedicationRequest.class));
		
		fhir.assertIsSatisfied();
		sqlOrderType.assertIsSatisfied();
		sqlOrders.assertIsSatisfied();
		sqlPreviousOrder.assertIsSatisfied();
	}
	
	@Test
	void shouldDropNonDrugOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(0);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			OperationOutcome operationOutcome = new OperationOutcome();
			fhirOutput.setBody(operationOutcome);
		});
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", TEST_ORDER_TYPE_UUID);
			sqlOutput.setBody(Collections.singletonList(map));
		});
		
		MockEndpoint sqlOrders = getMockEndpoint("mock:sql-orders");
		sqlOrders.expectedMessageCount(0);
		sqlOrders.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> output = new HashMap<>();
			output.put("voided", String.valueOf(0));
			output.put("order_action", "NEW");
			output.put("previous_order_id", String.valueOf(1));
			sqlOutput.setBody(Collections.singletonList(output));
		});
		
		MockEndpoint sqlPreviousOrder = getMockEndpoint("mock:sql-previous-order");
		sqlPreviousOrder.expectedMessageCount(0);
		sqlPreviousOrder.setResultWaitTime(100);
		sqlPreviousOrder.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", DRUG_ORDER_UUID)));
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
		fhir.assertIsSatisfied();
		sqlOrderType.assertIsSatisfied();
		sqlOrders.assertIsSatisfied();
		sqlPreviousOrder.assertIsSatisfied();
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
	
	@Test
	void shouldHandleDiscontinueDrugOrder() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			MedicationRequest medicationRequest = new MedicationRequest();
			medicationRequest.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(medicationRequest);
		});
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", DRUG_ORDER_TYPE_UUID);
			sqlOutput.setBody(Collections.singletonList(map));
		});
		
		MockEndpoint sqlOrders = getMockEndpoint("mock:sql-orders");
		sqlOrders.expectedMessageCount(1);
		sqlOrders.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, Object> output = new HashMap<>();
			output.put("voided", 0);
			output.put("order_action", "DISCONTINUE");
			output.put("previous_order_id", 1);
			sqlOutput.setBody(Collections.singletonList(output));
		});
		
		MockEndpoint sqlPreviousOrder = getMockEndpoint("mock:sql-previous-order");
		sqlPreviousOrder.expectedMessageCount(1);
		sqlPreviousOrder.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", DRUG_ORDER_UUID)));
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
		
		// Verify we got a medication request object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("d"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(MedicationRequest.class));
		
		fhir.assertIsSatisfied();
		sqlOrderType.assertIsSatisfied();
		sqlOrders.assertIsSatisfied();
		sqlPreviousOrder.assertIsSatisfied();
	}
}
