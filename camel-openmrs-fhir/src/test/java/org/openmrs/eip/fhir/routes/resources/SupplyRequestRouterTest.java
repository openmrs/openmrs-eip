package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.SUPPLY_REQUEST_ORDER_TYPE_UUID;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.camel.Endpoint;
import org.apache.camel.Message;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringTestSupport;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.hl7.fhir.r4.model.SupplyRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.fhir.spring.OpenmrsRestConfiguration;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

@UseAdviceWith
class SupplyRequestRouterTest extends CamelSpringTestSupport {
	
	private static final String MEDICAL_SUPPLY_ORDER_RESPONSE = "{\"uuid\":\"dd36be07-f4fe-46a4-b994-fb7eaf0bb473\",\"orderNumber\":\"ORD-1\",\"accessionNumber\":\"test_abc_medical_supply\",\"patient\":{\"uuid\":\"8b562184-bf0f-4ece-8852-780ee315f98e\",\"display\":\"H-3000000 - Siddharth Vaish\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/patient\\/8b562184-bf0f-4ece-8852-780ee315f98e\",\"resourceAlias\":\"patient\"}]},\"concept\":{\"uuid\":\"a1937e67-6e4e-4296-8a4c-255cd38fd1dc\",\"display\":\"Abaisse langue\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/concept\\/a1937e67-6e4e-4296-8a4c-255cd38fd1dc\",\"resourceAlias\":\"concept\"}]},\"action\":\"NEW\",\"careSetting\":{\"uuid\":\"6f0c9a92-6f24-11e3-af88-005056821db0\",\"display\":\"Outpatient\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/caresetting\\/6f0c9a92-6f24-11e3-af88-005056821db0\",\"resourceAlias\":\"caresetting\"}]},\"previousOrder\":null,\"dateActivated\":\"2025-01-08T08:44:03.000+0000\",\"scheduledDate\":null,\"dateStopped\":null,\"autoExpireDate\":null,\"encounter\":{\"uuid\":\"5a286408-167e-4883-aed8-61fb0b0e65ee\",\"display\":\"Orders 01\\/08\\/2025\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/encounter\\/5a286408-167e-4883-aed8-61fb0b0e65ee\",\"resourceAlias\":\"encounter\"}]},\"orderer\":{\"uuid\":\"adbc0d21-c77b-426e-929d-76ff08f9f250\",\"display\":\"admin - Super User\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/provider\\/adbc0d21-c77b-426e-929d-76ff08f9f250\",\"resourceAlias\":\"provider\"}]},\"orderReason\":null,\"orderReasonNonCoded\":null,\"orderType\":{\"uuid\":\"67a92bd6-0f88-11ea-8d71-362b9e155667\",\"display\":\"Medical Supply Order\",\"name\":\"Medical Supply Order\",\"javaClassName\":\"org.openmrs.module.orderexpansion.api.model.MedicalSupplyOrder\",\"retired\":false,\"description\":\"An order for Medical supplies\",\"conceptClasses\":[{\"uuid\":\"bfaba740-4c32-43c9-b64c-3e2c0c08cdf0\",\"display\":\"Material\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/conceptclass\\/bfaba740-4c32-43c9-b64c-3e2c0c08cdf0\",\"resourceAlias\":\"conceptclass\"}]}],\"parent\":null,\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/ordertype\\/67a92bd6-0f88-11ea-8d71-362b9e155667\",\"resourceAlias\":\"ordertype\"},{\"rel\":\"full\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/ordertype\\/67a92bd6-0f88-11ea-8d71-362b9e155667?v=full\",\"resourceAlias\":\"ordertype\"}],\"resourceVersion\":\"1.10\"},\"urgency\":\"ROUTINE\",\"instructions\":null,\"commentToFulfiller\":null,\"display\":\"Abaisse langue\",\"quantity\":10,\"medicalSuppliesInventoryId\":null,\"brandName\":null,\"quantityUnits\":{\"uuid\":\"162396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"display\":\"Box\",\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/concept\\/162396AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\",\"resourceAlias\":\"concept\"}]},\"links\":[{\"rel\":\"self\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/order\\/dd36be07-f4fe-46a4-b994-fb7eaf0bb473\",\"resourceAlias\":\"order\"},{\"rel\":\"full\",\"uri\":\"http:\\/\\/localhost\\/openmrs\\/ws\\/rest\\/v1\\/order\\/dd36be07-f4fe-46a4-b994-fb7eaf0bb473?v=full\",\"resourceAlias\":\"order\"}],\"type\":\"medicalsupplyorder\",\"resourceVersion\":\"1.10\"}";
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() {
		SupplyRequestRouter supplyRequestRouter = new SupplyRequestRouter();
		supplyRequestRouter.setOpenmrsRestConfiguration(new OpenmrsRestConfiguration());
		supplyRequestRouter.setOpenmrsBaseUrl("http://openmrs:8080/openmrs");
		supplyRequestRouter.setSupplyRequestOrderTypeUuid(SUPPLY_REQUEST_ORDER_TYPE_UUID);
		supplyRequestRouter.from(FhirResource.SUPPLYREQUEST.outgoingUrl()).to("mock:result");
		return supplyRequestRouter;
	}
	
	@Override
	public boolean isUseDebugger() {
		return true;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-supplyrequest-router", context, new AdviceWithRouteBuilder() {
			
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
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.SUPPLYREQUEST.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
	}
	
	@Test
	void shouldHandleSupplyRequestOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint http = getMockEndpoint("mock:http");
		http.expectedMessageCount(1);
		http.whenAnyExchangeReceived((exchange) -> {
			Message httpOutput = exchange.getMessage();
			httpOutput.setBody(MEDICAL_SUPPLY_ORDER_RESPONSE);
		});
		
		MockEndpoint sqlOrderType = getMockEndpoint("mock:sql-order-type");
		sqlOrderType.expectedMessageCount(1);
		sqlOrderType.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			Map<String, String> map = new HashMap<>();
			map.put("uuid", SUPPLY_REQUEST_ORDER_TYPE_UUID);
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
		
		// Verify we got a supply request object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(SupplyRequest.class));
		
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
