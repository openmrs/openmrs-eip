package org.openmrs.eip.camel.route;

import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.openmrs.eip.Constants.EX_PROP_CONCEPT_CODE;
import static org.openmrs.eip.Constants.EX_PROP_CONCEPT_SOURCE;
import static org.openmrs.eip.Constants.HTTP_HEADER_AUTH;
import static org.openmrs.eip.Constants.ROUTE_ID_GET_CONCEPT_BY_MAPPING;
import static org.openmrs.eip.Constants.URI_GET_CONCEPT_BY_MAPPING;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ToDynamicDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.AppContext;
import org.openmrs.eip.BaseCamelTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.liquibase.enabled=false")
@TestPropertySource(properties = "camel.springboot.xml-routes=classpath*:camel/" + ROUTE_ID_GET_CONCEPT_BY_MAPPING + ".xml")
@TestPropertySource(properties = "logging.level." + ROUTE_ID_GET_CONCEPT_BY_MAPPING + "=DEBUG")
@TestPropertySource(properties = "logging.level.org.apache.camel.reifier.RouteReifier=WARN")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = "openmrs.baseUrl=" + GetConceptByMappingFromOpenmrsRouteTest.OPENMRS_URL)
@TestPropertySource(properties = "openmrs.username=" + GetConceptByMappingFromOpenmrsRouteTest.OPENMRS_USER)
@TestPropertySource(properties = "openmrs.password=" + GetConceptByMappingFromOpenmrsRouteTest.OPENMRS_PASS)
public class GetConceptByMappingFromOpenmrsRouteTest extends BaseCamelTest {
	
	private static final String MAP_KEY = ROUTE_ID_GET_CONCEPT_BY_MAPPING + "-sourceAndCodeToConceptMapKey";
	
	protected static final String OPENMRS_URL = "http://test.com";
	
	protected static final String OPENMRS_USER = "test-user";
	
	protected static final String OPENMRS_PASS = "test-pass";
	
	private String openmrsAuth;
	
	@EndpointInject("mock:http")
	private MockEndpoint mockHttpEndpoint;
	
	@EndpointInject("mock:processor")
	private MockEndpoint mockProcessor;
	
	@Before
	public void setup() throws Exception {
		AppContext.remove(MAP_KEY);
		mockHttpEndpoint.reset();
		mockProcessor.reset();
		final String openmrsUser = env.getProperty("openmrs.username");
		final String openmrsPassword = env.getProperty("openmrs.password");
		openmrsAuth = "Basic " + Base64.getEncoder().encodeToString((openmrsUser + ":" + openmrsPassword).getBytes());
		
		advise(ROUTE_ID_GET_CONCEPT_BY_MAPPING, new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ProcessDefinition.class).replace().to(mockProcessor);
				weaveByType(ToDynamicDefinition.class).replace().to(mockHttpEndpoint);
			}
			
		});
	}
	
	@Test
	public void shouldReturnTheCachedConceptIfItAlreadyExists() throws Exception {
		final String source = "CIEL";
		final String code = "12345";
		final Map expectedConcept = singletonMap("uuid", "some-concept-uuid");
		AppContext.add(MAP_KEY, singletonMap(source + ":" + code, expectedConcept));
		final Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_CONCEPT_SOURCE, source);
		exchange.setProperty(EX_PROP_CONCEPT_CODE, code);
		mockProcessor.expectedMessageCount(0);
		mockHttpEndpoint.expectedMessageCount(0);
		
		producerTemplate.send(URI_GET_CONCEPT_BY_MAPPING, exchange);
		
		mockProcessor.assertIsSatisfied();
		mockHttpEndpoint.assertIsSatisfied();
		assertEquals(expectedConcept, exchange.getIn().getBody(Map.class));
	}
	
	@Test
	public void shouldFetchTheConceptIfDoesNotExistInTheCache() throws Exception {
		final String source = "CIEL";
		final String code = "12345";
		final String expectedConceptUuid = "some-concept-uuid";
		final Map expectedConcept = singletonMap("uuid", expectedConceptUuid);
		AppContext.add(MAP_KEY, new HashMap());
		final Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_CONCEPT_SOURCE, source);
		exchange.setProperty(EX_PROP_CONCEPT_CODE, code);
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setBody("{\"results\":[{\"uuid\":\"" + expectedConceptUuid + "\"}]}");
		});
		
		producerTemplate.send(URI_GET_CONCEPT_BY_MAPPING, exchange);
		
		mockProcessor.assertIsSatisfied();
		mockHttpEndpoint.assertIsSatisfied();
		assertEquals(expectedConcept, exchange.getIn().getBody(Map.class));
		assertEquals(openmrsAuth, exchange.getIn().getHeader(HTTP_HEADER_AUTH));
		assertEquals("GET", exchange.getIn().getHeader(Exchange.HTTP_METHOD));
		assertEquals("source=" + source + "&code=" + code, exchange.getIn().getHeader(Exchange.HTTP_RAW_QUERY));
		
	}
	
	@Test
	public void shouldFetchTheConceptIfDoesNotExistInTheCacheAndTheCacheIsNotYetInitiated() throws Exception {
		assertNull(AppContext.get(MAP_KEY));
		final String source = "CIEL";
		final String code = "12345";
		final String expectedConceptUuid = "some-concept-uuid";
		final Map expectedConcept = singletonMap("uuid", expectedConceptUuid);
		final Exchange exchange = new DefaultExchange(camelContext);
		exchange.setProperty(EX_PROP_CONCEPT_SOURCE, source);
		exchange.setProperty(EX_PROP_CONCEPT_CODE, code);
		mockProcessor.expectedMessageCount(1);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setBody("{\"results\":[{\"uuid\":\"" + expectedConceptUuid + "\"}]}");
		});
		
		producerTemplate.send(URI_GET_CONCEPT_BY_MAPPING, exchange);
		
		mockProcessor.assertIsSatisfied();
		mockHttpEndpoint.assertIsSatisfied();
		assertEquals(expectedConcept, exchange.getIn().getBody(Map.class));
		assertEquals(openmrsAuth, exchange.getIn().getHeader(HTTP_HEADER_AUTH));
		assertEquals("GET", exchange.getIn().getHeader(Exchange.HTTP_METHOD));
		assertEquals("source=" + source + "&code=" + code, exchange.getIn().getHeader(Exchange.HTTP_RAW_QUERY));
	}
	
	@Test
	public void shouldUserTheOauthHeaderToAuthenticateIfItExists() throws Exception {
		final String source = "CIEL";
		final String code = "12345";
		final String expectedConceptUuid = "some-concept-uuid";
		final Map expectedConcept = singletonMap("uuid", expectedConceptUuid);
		AppContext.add(MAP_KEY, new HashMap());
		final Exchange exchange = new DefaultExchange(camelContext);
		final String oauthHeader = "Bearer oauth-token";
		mockProcessor.expectedMessageCount(1);
		mockProcessor.whenAnyExchangeReceived(e -> {
			e.getIn().setBody(oauthHeader);
		});
		
		exchange.setProperty(EX_PROP_CONCEPT_SOURCE, source);
		exchange.setProperty(EX_PROP_CONCEPT_CODE, code);
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(HTTP_HEADER_AUTH, oauthHeader);
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setBody("{\"results\":[{\"uuid\":\"" + expectedConceptUuid + "\"}]}");
		});
		
		producerTemplate.send(URI_GET_CONCEPT_BY_MAPPING, exchange);
		
		mockProcessor.assertIsSatisfied();
		mockHttpEndpoint.assertIsSatisfied();
		assertEquals(expectedConcept, exchange.getIn().getBody(Map.class));
		assertEquals(oauthHeader, exchange.getIn().getHeader(HTTP_HEADER_AUTH));
		assertEquals("GET", exchange.getIn().getHeader(Exchange.HTTP_METHOD));
		assertEquals("source=" + source + "&code=" + code, exchange.getIn().getHeader(Exchange.HTTP_RAW_QUERY));
	}
	
}
