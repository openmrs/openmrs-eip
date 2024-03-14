package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_IDENTIFIER;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_OPERATION;
import static org.openmrs.eip.fhir.Constants.PROP_EVENT_TABLE_NAME;

import java.io.IOException;
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
import org.hl7.fhir.r4.model.Observation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import ca.uhn.fhir.context.FhirContext;

@UseAdviceWith
public class ObservationFhirRouterTest extends CamelSpringTestSupport {
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() throws Exception {
		RouteBuilder rb = new ObservationFhirRouter();
		rb.from(FhirResource.OBSERVATION.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-observation-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.OBSERVATION.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
	}
	
	@Test
	void shouldHandleObservationEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Observation observation = new Observation();
			observation.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(observation);
		});
		
		// Act
		template.send((exchange) -> {
			exchange.setProperty(PROP_EVENT_TABLE_NAME, "obs");
			exchange.setProperty(PROP_EVENT_OPERATION, "c");
			exchange.setProperty(PROP_EVENT_IDENTIFIER, Uuid.randomUuid().toString());
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a observation object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Observation observation = FhirContext.forR4().newJsonParser().parseResource(Observation.class,
		    (InputStream) messageBody);
		assertThat(observation, notNullValue());
		
		fhir.assertIsSatisfied();
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
			exchange.setProperty(PROP_EVENT_TABLE_NAME, "unknown_table");
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
