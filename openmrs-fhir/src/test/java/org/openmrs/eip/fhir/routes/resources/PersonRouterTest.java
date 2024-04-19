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
import org.apache.commons.collections.map.SingletonMap;
import org.hl7.fhir.r4.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.fhir.FhirResource;
import org.openmrs.eip.mysql.watcher.Event;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

@UseAdviceWith
public class PersonRouterTest extends CamelSpringTestSupport {
	
	private MockEndpoint sqlPersonVoided;
	
	private MockEndpoint sqlPersonUuid;
	
	private MockEndpoint sqlPersonOtherVoided;
	
	@Override
	protected AbstractApplicationContext createApplicationContext() {
		return new StaticApplicationContext();
	}
	
	@Override
	protected RoutesBuilder createRouteBuilder() {
		RouteBuilder rb = new PersonRouter();
		rb.from(FhirResource.PERSON.outgoingUrl()).to("mock:result");
		return rb;
	}
	
	@BeforeEach
	void setup() throws Exception {
		AdviceWith.adviceWith("fhir-person-router", context, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				weaveByToUri("fhir:*").replace().to("mock:fhir");
				weaveByToUri(
				    "sql:SELECT voided FROM person WHERE uuid = '${exchangeProperty.event.identifier}'?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-person-voided");
				weaveByToUri(
				    "sql:SELECT voided FROM person WHERE person_id = (SELECT t.person_id FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-person-other-voided");
				weaveByToUri(
				    "sql:SELECT uuid FROM person WHERE person_id = (SELECT t.person FROM ${exchangeProperty.event.tableName} t WHERE t.uuid = '${exchangeProperty.event.identifier}')?dataSource=#openmrsDataSource")
				            .replace().to("mock:sql-person-uuid");
			}
		});
		
		Endpoint defaultEndpoint = context.getEndpoint(FhirResource.PERSON.incomingUrl());
		template.setDefaultEndpoint(defaultEndpoint);
		
		sqlPersonVoided = getMockEndpoint("mock:sql-person-voided");
		sqlPersonOtherVoided = getMockEndpoint("mock:sql-person-other-voided");
		sqlPersonUuid = getMockEndpoint("mock:sql-person-uuid");
	}
	
	private void setupExpectations() {
		sqlPersonVoided.expectedMessageCount(1);
		sqlPersonVoided.setResultWaitTime(100);
		sqlPersonVoided.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(new SingletonMap("voided", 0)));
		});
		
		sqlPersonOtherVoided.expectedMessageCount(0);
		sqlPersonOtherVoided.setResultWaitTime(100);
		sqlPersonOtherVoided.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(new SingletonMap("voided", 0)));
		});
		
		sqlPersonUuid.expectedMessageCount(0);
		sqlPersonUuid.setResultWaitTime(100);
		sqlPersonUuid.whenAnyExchangeReceived((exchange) -> {
			Message sqlOutput = exchange.getMessage();
			sqlOutput.setBody(Collections.singletonList(new SingletonMap("uuid", UUID.randomUUID().toString())));
		});
	}
	
	private void verifyExpectations() throws InterruptedException {
		sqlPersonVoided.assertIsSatisfied();
		sqlPersonOtherVoided.assertIsSatisfied();
		sqlPersonUuid.assertIsSatisfied();
	}
	
	@Test
	void shouldHandlePersonEntry() throws InterruptedException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.expectedMessageCount(1);
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Person person = new Person();
			person.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(person);
		});
		
		setupExpectations();
		
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
		
		// Verify we got a person object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(Person.class));
		
		fhir.assertIsSatisfied();
		verifyExpectations();
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
			Person person = new Person();
			person.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(person);
		});
		fhir.expectedMessageCount(1);
		fhir.setResultWaitTime(100);
		
		setupExpectations();
		// Override some expectations
		this.sqlPersonUuid.expectedMessageCount(1);
		this.sqlPersonVoided.expectedMessageCount(0);
		this.sqlPersonOtherVoided.expectedMessageCount(1);
		
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
		
		// Verify we got a person object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(Person.class));
		
		fhir.assertIsSatisfied();
		verifyExpectations();
	}
	
	@Test
	void shouldHandlePersonAddressEntry() throws InterruptedException, IOException {
		// Arrange
		MockEndpoint result = getMockEndpoint("mock:result");
		result.expectedMessageCount(1);
		result.setResultWaitTime(100);
		
		MockEndpoint fhir = getMockEndpoint("mock:fhir");
		fhir.whenAnyExchangeReceived((exchange) -> {
			Message fhirOutput = exchange.getMessage();
			Person person = new Person();
			person.setId(UUID.randomUUID().toString());
			fhirOutput.setBody(person);
		});
		fhir.expectedMessageCount(1);
		fhir.setResultWaitTime(100);
		
		setupExpectations();
		// Override some expectations
		this.sqlPersonUuid.expectedMessageCount(1);
		this.sqlPersonVoided.expectedMessageCount(0);
		this.sqlPersonOtherVoided.expectedMessageCount(1);
		
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
		
		// Verify we got a person object
		Message message = result.getExchanges().get(0).getMessage();
		assertThat(message.getHeader(HEADER_FHIR_EVENT_TYPE), equalTo("c"));
		
		Object messageBody = message.getBody();
		assertThat(messageBody, notNullValue());
		assertThat(messageBody, instanceOf(Person.class));
		
		fhir.assertIsSatisfied();
		verifyExpectations();
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
