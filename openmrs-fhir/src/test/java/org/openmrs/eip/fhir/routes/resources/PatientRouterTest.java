package org.openmrs.eip.fhir.routes.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;

import java.io.IOException;
import java.util.Collections;
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
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

@UseAdviceWith
public class PatientRouterTest extends CamelSpringTestSupport {
	
	private MockEndpoint sqlVoidedMockEndpoint;
	
	private MockEndpoint sqlPatientVoidedMockEndpoint;
	
	private MockEndpoint sqlPatientIdentifierVoidedMockEndpoint;
	
	private MockEndpoint sqlPatientIdentifierUuidMockEndpoint;
	
	private MockEndpoint sqlPatientUuidMockEndpoint;
	
	private MockEndpoint sqlPatientMockEndpoint;
	
	private MockEndpoint sqlPatientExistMockEndpoint;
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() throws Exception {
		RouteBuilder rb = new PatientRouter();
		rb.from(FhirResource.PATIENT.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-patient-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
				weaveByToUri(
				    "sql:SELECT voided FROM person WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient-voided");
				weaveByToUri(
				    "sql:SELECT voided FROM person WHERE person_id = (SELECT t.patient_id FROM patient_identifier t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient-identifier-voided");
				weaveByToUri(
				    "sql:SELECT voided FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-voided");
				weaveByToUri(
				    "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient-uuid");
				weaveByToUri(
				    "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.patient_id FROM patient_identifier t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient-identifier-uuid");
				weaveByToUri(
				    "sql:SELECT patient_id FROM patient WHERE patient_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient");
				weaveByToUri(
				    "sql:SELECT patient_id FROM patient WHERE patient_id = (SELECT t.person_id FROM person t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-patient-uuid");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.PATIENT.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
		
		sqlVoidedMockEndpoint = getMockEndpoint("mock:sql-voided");
		sqlPatientVoidedMockEndpoint = getMockEndpoint("mock:sql-patient-voided");
		sqlPatientIdentifierVoidedMockEndpoint = getMockEndpoint("mock:sql-patient-identifier-voided");
		
		sqlPatientIdentifierUuidMockEndpoint = getMockEndpoint("mock:sql-patient-identifier-uuid");
		sqlPatientUuidMockEndpoint = getMockEndpoint("mock:sql-patient-uuid");
		sqlPatientMockEndpoint = getMockEndpoint("mock:sql-patient");
		sqlPatientExistMockEndpoint = getMockEndpoint("mock:sql-patient-exist");
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
		
		// set up expectations for the sql endpoints
		this.setupExpectations();
		sqlPatientExistMockEndpoint.expectedMessageCount(1);
		sqlPatientUuidMockEndpoint.expectedMessageCount(1);
		
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
		assertThat(messageBody, instanceOf(Patient.class));
		
		fhir.assertIsSatisfied();
		verifyEndpoints();
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
		
		this.setupExpectations();
		sqlPatientExistMockEndpoint.expectedMessageCount(1);
		sqlPatientUuidMockEndpoint.expectedMessageCount(1);
		
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
		assertThat(messageBody, instanceOf(Patient.class));
		
		fhir.assertIsSatisfied();
		verifyEndpoints();
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
		
		this.setupExpectations();
		this.sqlPatientVoidedMockEndpoint.expectedMessageCount(0);
		this.sqlPatientIdentifierVoidedMockEndpoint.expectedMessageCount(1);
		this.sqlPatientIdentifierUuidMockEndpoint.expectedMessageCount(1);
		
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
		assertThat(messageBody, instanceOf(Patient.class));
		
		fhir.assertIsSatisfied();
		verifyEndpoints();
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
		
		this.setupExpectations();
		this.sqlPatientUuidMockEndpoint.expectedMessageCount(1);
		this.sqlVoidedMockEndpoint.expectedMessageCount(1);
		this.sqlPatientVoidedMockEndpoint.expectedMessageCount(0);
		
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
		assertThat(messageBody, instanceOf(Patient.class));
		
		fhir.assertIsSatisfied();
		this.verifyEndpoints();
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
		
		this.setupExpectations();
		this.sqlPatientUuidMockEndpoint.expectedMessageCount(1);
		this.sqlVoidedMockEndpoint.expectedMessageCount(1);
		this.sqlPatientVoidedMockEndpoint.expectedMessageCount(0);
		
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
		assertThat(messageBody, instanceOf(Patient.class));
		
		fhir.assertIsSatisfied();
		this.verifyEndpoints();
	}
	
	@Test
	void shouldSkipNonPatientEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(0);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(0);
		
		MockEndpoint sql = getMockEndpoint("mock:sql");
		sql.expectedMessageCount(0);
		
		sqlPatientExistMockEndpoint.setResultWaitTime(100);
		sqlPatientExistMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody("");
		});
		
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
		fhir.assertIsSatisfied();
		sql.assertIsSatisfied();
		sqlPatientExistMockEndpoint.assertIsSatisfied();
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
	
	private void verifyEndpoints() throws InterruptedException {
		sqlPatientUuidMockEndpoint.assertIsSatisfied();
		sqlPatientIdentifierUuidMockEndpoint.assertIsSatisfied();
		sqlVoidedMockEndpoint.assertIsSatisfied();
		sqlPatientVoidedMockEndpoint.assertIsSatisfied();
		sqlPatientIdentifierVoidedMockEndpoint.assertIsSatisfied();
	}
	
	private void setupExpectations() {
		// set up expectations for the sql endpoints
		sqlPatientVoidedMockEndpoint.expectedMessageCount(1);
		sqlPatientVoidedMockEndpoint.setResultWaitTime(100);
		sqlPatientVoidedMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("voided", 0)));
		});
		
		sqlPatientIdentifierVoidedMockEndpoint.expectedMessageCount(0);
		sqlPatientIdentifierVoidedMockEndpoint.setResultWaitTime(100);
		sqlPatientIdentifierVoidedMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("voided", 0)));
		});
		
		sqlVoidedMockEndpoint.expectedMessageCount(0);
		sqlVoidedMockEndpoint.setResultWaitTime(100);
		sqlVoidedMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("voided", 0)));
		});
		
		sqlPatientIdentifierUuidMockEndpoint.expectedMessageCount(0);
		sqlPatientIdentifierUuidMockEndpoint.setResultWaitTime(100);
		sqlPatientIdentifierUuidMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", UUID.randomUUID().toString())));
		});
		
		sqlPatientUuidMockEndpoint.expectedMessageCount(0);
		sqlPatientUuidMockEndpoint.setResultWaitTime(100);
		sqlPatientUuidMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("uuid", UUID.randomUUID().toString())));
		});
		
		sqlPatientMockEndpoint.expectedMessageCount(0);
		sqlPatientMockEndpoint.setResultWaitTime(100);
		sqlPatientMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("patient_id", 123)));
		});
		
		sqlPatientExistMockEndpoint.expectedMessageCount(0);
		sqlPatientExistMockEndpoint.setResultWaitTime(100);
		sqlPatientExistMockEndpoint.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(Collections.singletonMap("patient_id", 123)));
		});
	}
}
