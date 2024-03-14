package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_IDENTIFIER;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import java.io.InputStream;
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
import org.apache.kafka.common.Uuid;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import ca.uhn.fhir.context.FhirContext;

@UseAdviceWith
class ServiceRequestRouterTest extends CamelSpringTestSupport {
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() {
		RouteBuilder rb = new ServiceRequestRouter();
		rb.from(FhirResource.SERVICEREQUEST.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@Override
	public boolean isUseDebugger() {
		return true;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-servicerequest-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.SERVICEREQUEST.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
	}
	
	@Test
	void shouldHandleTestOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			ServiceRequest serviceRequest = new ServiceRequest();
			serviceRequest.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(serviceRequest);
		});
		
		// Act
		template.send((exchange) -> {
			exchange.setProperty(PROP_EVENT_TABLE_NAME, "test_order");
			exchange.setProperty(PROP_EVENT_OPERATION, "c");
			exchange.setProperty(PROP_EVENT_IDENTIFIER, Uuid.randomUuid().toString());
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
		assertThat(messageBody, instanceOf(InputStream.class));
		
		ServiceRequest serviceRequest = FhirContext.forR4().newJsonParser().parseResource(ServiceRequest.class,
		    (InputStream) messageBody);
		assertThat(serviceRequest, notNullValue());
		
		fhir.assertIsSatisfied();
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
			ServiceRequest serviceRequest = new ServiceRequest();
			serviceRequest.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(serviceRequest);
		});
		
		// Act
		template.send((exchange) -> {
			exchange.setProperty(PROP_EVENT_TABLE_NAME, "orders");
			exchange.setProperty(PROP_EVENT_OPERATION, "c");
			exchange.setProperty(PROP_EVENT_IDENTIFIER, Uuid.randomUuid().toString());
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
		assertThat(messageBody, instanceOf(InputStream.class));
		
		ServiceRequest serviceRequest = FhirContext.forR4().newJsonParser().parseResource(ServiceRequest.class,
		    (InputStream) messageBody);
		assertThat(serviceRequest, notNullValue());
		
		fhir.assertIsSatisfied();
	}
	
	@Test
	void shouldDropNonDrugOrderEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			OperationOutcome operationOutcome = new OperationOutcome();
			fhirOutput.setBody(operationOutcome);
		});
		
		// Act
		template.send((exchange) -> {
			exchange.setProperty(PROP_EVENT_TABLE_NAME, "orders");
			exchange.setProperty(PROP_EVENT_OPERATION, "c");
			exchange.setProperty(PROP_EVENT_IDENTIFIER, Uuid.randomUuid().toString());
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		fhir.assertIsSatisfied();
	}
}
