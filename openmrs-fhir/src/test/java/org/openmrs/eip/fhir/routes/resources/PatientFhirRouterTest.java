package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
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
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import ca.uhn.fhir.context.FhirContext;

@UseAdviceWith
public class PatientFhirRouterTest extends CamelSpringTestSupport {
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() throws Exception {
		RouteBuilder rb = new PatientFhirRouter();
		rb.from(FhirResource.PATIENT.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-patient-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
				weaveByToUri("sql:*").replace().to("mock:sql");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.PATIENT.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
		
	}
	
	@Test
	void shouldHandlePatientEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Patient patient = new Patient();
			patient.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(patient);
		});
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.expectedMessageCount(0);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("patient");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a patient object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Patient patient = FhirContext.forR4().newJsonParser().parseResource(Patient.class, (InputStream) messageBody);
		assertThat(patient, notNullValue());
		
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
	}
	
	@Test
	void shouldHandlePersonEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Patient patient = new Patient();
			patient.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(patient);
		});
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.expectedMessageCount(0);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("person");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a patient object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Patient patient = FhirContext.forR4().newJsonParser().parseResource(Patient.class, (InputStream) messageBody);
		assertThat(patient, notNullValue());
		
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
	}
	
	@Test
	void shouldHandlePatientIdentifierEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Patient patient = new Patient();
			patient.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(patient);
		});
		fhir.expectedMessageCount(1);
		fhir.setResultWaitTime(100);
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			List<Object> results = List.of(new Object() {
				
				@SuppressWarnings("unused")
				public String get(String attribute) {
					return Uuid.randomUuid().toString();
				}
			});
			sqlOutput.setBody(results);
		});
		sql.expectedMessageCount(1);
		sql.setResultWaitTime(100);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("patient_identifier");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a patient object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Patient patient = FhirContext.forR4().newJsonParser().parseResource(Patient.class, (InputStream) messageBody);
		assertThat(patient, notNullValue());
		
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
	}
	
	@Test
	void shouldHandlePersonNameEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Patient patient = new Patient();
			patient.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(patient);
		});
		fhir.expectedMessageCount(1);
		fhir.setResultWaitTime(100);
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			List<Object> results = List.of(new Object() {
				
				@SuppressWarnings("unused")
				public String get(String attribute) {
					return Uuid.randomUuid().toString();
				}
			});
			sqlOutput.setBody(results);
		});
		sql.expectedMessageCount(1);
		sql.setResultWaitTime(100);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("person_name");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a patient object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Patient patient = FhirContext.forR4().newJsonParser().parseResource(Patient.class, (InputStream) messageBody);
		assertThat(patient, notNullValue());
		
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
	}
	
	@Test
	void shouldHandlePersonAddress() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Patient patient = new Patient();
			patient.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(patient);
		});
		fhir.expectedMessageCount(1);
		fhir.setResultWaitTime(100);
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			List<Object> results = List.of(new Object() {
				
				@SuppressWarnings("unused")
				public String get(String attribute) {
					return Uuid.randomUuid().toString();
				}
			});
			sqlOutput.setBody(results);
		});
		sql.expectedMessageCount(1);
		sql.setResultWaitTime(100);
		
		// Act
		template.send((exchange) -> {
			Event event = new Event();
			event.setTableName("person_address");
			event.setOperation("c");
			event.setIdentifier(UUID.randomUUID().toString());
			exchange.setProperty("event", event);
			Message in = exchange.getIn();
			in.setBody("");
		});
		
		// Assert
		result.assertIsSatisfied();
		
		// Verify we got a patient object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(InputStream.class));
		
		Patient patient = FhirContext.forR4().newJsonParser().parseResource(Patient.class, (InputStream) messageBody);
		assertThat(patient, notNullValue());
		
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
	}
	
	@Test
	void shouldSkipUnknownEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(0);
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.expectedMessageCount(0);
		
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
		sql.assertIsSatisfied();
	}
}
