package org.openmrs.eip.camel.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.eip.Constants.HTTP_HEADER_AUTH;
import static org.openmrs.eip.Constants.ROUTE_ID_GET_ENTITY_BY_ID;
import static org.openmrs.eip.Constants.URI_GET_ENTITY_BY_ID;

import java.util.Base64;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ToDynamicDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.BaseCamelTest;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.TestConstants;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import ch.qos.logback.classic.Level;

@TestPropertySource(properties = "spring.liquibase.enabled=false")
@TestPropertySource(properties = "openmrs.baseUrl=" + GetEntityByUuidFromOpenmrsRouteTest.OPENMRS_URL)
@TestPropertySource(properties = "openmrs.username=" + GetEntityByUuidFromOpenmrsRouteTest.OPENMRS_USER)
@TestPropertySource(properties = "openmrs.password=" + GetEntityByUuidFromOpenmrsRouteTest.OPENMRS_PASS)
@TestPropertySource(properties = "camel.springboot.routes-include-pattern=classpath:camel/" + ROUTE_ID_GET_ENTITY_BY_ID
        + ".xml")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_GET_ENTITY_BY_ID + "=DEBUG")
@TestPropertySource(properties = "logging.level.org.apache.camel.reifier.RouteReifier=WARN")
@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=true")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class GetEntityByUuidFromOpenmrsRouteTest extends BaseCamelTest {
	
	protected static final String OPENMRS_URL = "http://test.com";
	
	protected static final String OPENMRS_USER = "test-user";
	
	protected static final String OPENMRS_PASS = "test-pass";
	
	protected static final String EX_PROP_RES_NAME = "resourceName";
	
	protected static final String EX_PROP_RES_ID = "resourceId";
	
	protected static final String EX_PROP_RES_URL = "resourceUrl";
	
	protected static final String EX_PROP_RES_REP = "resourceRepresentation";
	
	protected static final String RES_NAME = "person";
	
	protected static final String RES_ID = "person-uuid";
	
	@EndpointInject("mock:http")
	private MockEndpoint mockHttpEndpoint;
	
	@EndpointInject("mock:processor")
	private MockEndpoint mockProcessor;
	
	@BeforeEach
	public void setup() throws Exception {
		mockHttpEndpoint.reset();
		mockProcessor.reset();
		
		advise(ROUTE_ID_GET_ENTITY_BY_ID, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockProcessor);
				weaveByType(ToDynamicDefinition.class).replace().to(mockHttpEndpoint);
				onException(EIPException.class).to(TestConstants.URI_TEST_ERROR_HANDLER);
			}
		});
		
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		mockHttpEndpoint.assertIsSatisfied();
		mockProcessor.assertIsSatisfied();
	}
	
	@Test
	public void shouldFetchAndReturnAResourceFromOpenmrs() {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID;
		final String auth = "Basic " + Base64.getEncoder().encodeToString((OPENMRS_USER + ":" + OPENMRS_PASS).getBytes());
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, auth);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_RAW_QUERY, null);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		final String expectedResponse = "{\"uuid\":\"" + RES_ID + "\"}";
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			e.getIn().setBody(expectedResponse);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertEquals(expectedResponse, exchange.getIn().getBody());
	}
	
	@Test
	public void shouldFetchAndReturnASubResourceFromOpenmrs() {
		Exchange exchange = new DefaultExchange(camelContext);
		final String exPropSubResName = "subResourceName";
		final String exPropSubResId = "subResourceId";
		final String subResName = "name";
		final String subResId = "name-uuid";
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		exchange.setProperty(exPropSubResName, subResName);
		exchange.setProperty("isSubResource", true);
		exchange.setProperty(exPropSubResId, subResId);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID + "/" + subResName + "/" + subResId;
		final String auth = "Basic " + Base64.getEncoder().encodeToString((OPENMRS_USER + ":" + OPENMRS_PASS).getBytes());
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, auth);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		final String expectedResponse = "{\"uuid\":\"" + subResId + "\"}";
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			e.getIn().setBody(expectedResponse);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertEquals(expectedResponse, exchange.getIn().getBody());
	}
	
	@Test
	public void shouldSetRepresentationIfSpecified() {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		final String rep = "ref";
		exchange.setProperty(EX_PROP_RES_REP, rep);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID;
		final String auth = "Basic " + Base64.getEncoder().encodeToString((OPENMRS_USER + ":" + OPENMRS_PASS).getBytes());
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, auth);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_RAW_QUERY, "v=" + rep);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		final String expectedResponse = "{\"uuid\":\"" + RES_ID + "\"}";
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			e.getIn().setBody(expectedResponse);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertEquals(expectedResponse, exchange.getIn().getBody());
	}
	
	@Test
	public void shouldUseTheOauthHeaderToAuthenticateIfItExists() {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID;
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockProcessor.expectedMessageCount(1);
		final String oauthHeader = "Bearer oauth-token";
		mockProcessor.whenAnyExchangeReceived(e -> {
			e.getIn().setBody(oauthHeader);
		});
		
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, oauthHeader);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_RAW_QUERY, null);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		final String expectedResponse = "{\"uuid\":\"" + RES_ID + "\"}";
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			e.getIn().setBody(expectedResponse);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertEquals(expectedResponse, exchange.getIn().getBody());
	}
	
	@Test
	public void shouldSetBodyToNullIfNoResourceIsFound() {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID;
		final String auth = "Basic " + Base64.getEncoder().encodeToString((OPENMRS_USER + ":" + OPENMRS_PASS).getBytes());
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, auth);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		final String errorMsg = "{fail}";
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 404);
			e.getIn().setBody(errorMsg);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertNull(exchange.getIn().getBody());
		assertMessageLogged(Level.INFO, "Resource Not Found -> Response Status Code: 404, Response Body: " + errorMsg);
	}
	
	@Test
	public void shouldFailIfTheStatusCodeIsNot200() {
		Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_RES_NAME, RES_NAME);
		exchange.setProperty(EX_PROP_RES_ID, RES_ID);
		final String url = OPENMRS_URL + "/ws/rest/v1/" + RES_NAME + "/" + RES_ID;
		final String auth = "Basic " + Base64.getEncoder().encodeToString((OPENMRS_USER + ":" + OPENMRS_PASS).getBytes());
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "GET");
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, auth);
		mockHttpEndpoint.expectedPropertyReceived(EX_PROP_RES_URL, url);
		mockHttpEndpoint.expectedHeaderReceived(EX_PROP_RES_REP, null);
		mockHttpEndpoint.expectedBodiesReceived((Object) null);
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 301);
		});
		
		producerTemplate.send(URI_GET_ENTITY_BY_ID, exchange);
		
		assertTrue(exchange.isFailed());
	}
}
