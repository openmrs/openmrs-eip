package org.openmrs.eip.camel.route;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.openmrs.eip.camel.OauthProcessor.FIELD_TOKEN;

import java.util.Collections;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ToDynamicDefinition;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openmrs.eip.BaseCamelTest;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.TestConstants;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "oauth.access.token.uri=" + OauthRouteTest.OAUTH_TOKEN_URL)
@TestPropertySource(properties = "oauth.client.id=" + OauthRouteTest.CLIENT_ID)
@TestPropertySource(properties = "oauth.client.secret=" + OauthRouteTest.CLIENT_SECRET)
@TestPropertySource(properties = "oauth.client.scope=" + OauthRouteTest.CLIENT_SCOPE)
@TestPropertySource(properties = "spring.liquibase.enabled=false")
@TestPropertySource(properties = "logging.level.org.apache.camel.reifier.RouteReifier=WARN")
@TestPropertySource(properties = "camel.springboot.routes-collector-enabled=true")
@TestPropertySource(properties = "camel.springboot.routes-include-pattern=classpath:camel/oauth.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OauthRouteTest extends BaseCamelTest {
	
	private static final String URI = "direct:oauth";
	
	public static final String OAUTH_TOKEN_URL = "http://oauth.test";
	
	public static final String GRANT_TYPE_CREDS = "grant_type=client_credentials";
	
	public static final String CLIENT_ID = "test_id";
	
	public static final String CLIENT_SECRET = "test_secret";
	
	public static final String CLIENT_SCOPE = "test_scope";
	
	public static final String APPLICATION_URL_FORM = "application/x-www-form-urlencoded";
	
	@EndpointInject("mock:http")
	private MockEndpoint mockHttpEndpoint;
	
	@BeforeEach
	public void setup() throws Exception {
		mockHttpEndpoint.reset();
	}
	
	@Test
	public void shouldFetchTheAuthToken() throws Exception {
		advise("oauth", new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				weaveByType(ToDynamicDefinition.class).replace().to(mockHttpEndpoint);
			}
		});
		
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, APPLICATION_URL_FORM);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "POST");
		mockHttpEndpoint.expectedBodiesReceived(
		    GRANT_TYPE_CREDS + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&scope=" + CLIENT_SCOPE);
		final String expectedToken = "test-token";
		mockHttpEndpoint.whenAnyExchangeReceived(exchange -> {
			exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
			exchange.getIn().setBody("{\"" + FIELD_TOKEN + "\":\"" + expectedToken + "\"}");
		});
		
		Exchange exchange = new DefaultExchange(camelContext);
		
		producerTemplate.send(URI, exchange);
		
		mockHttpEndpoint.assertIsSatisfied();
		
		assertEquals(Collections.singletonMap(FIELD_TOKEN, expectedToken), exchange.getIn().getBody());
	}
	
	@Test
	public void shouldFailIfTheResponseStatusCodeIsNot200() throws Exception {
		advise("oauth", new AdviceWithRouteBuilder() {
			
			@Override
			public void configure() {
				onException(EIPException.class).to(TestConstants.URI_TEST_ERROR_HANDLER);
				weaveByType(ToDynamicDefinition.class).replace().to(mockHttpEndpoint);
			}
		});
		
		final int code = 401;
		mockHttpEndpoint.expectedMessageCount(1);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.CONTENT_TYPE, APPLICATION_URL_FORM);
		mockHttpEndpoint.expectedHeaderReceived(Exchange.HTTP_METHOD, "POST");
		mockHttpEndpoint.expectedBodiesReceived(
		    GRANT_TYPE_CREDS + "&client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&scope=" + CLIENT_SCOPE);
		mockHttpEndpoint.whenAnyExchangeReceived(e -> {
			e.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, 401);
		});
		
		Exchange exchange = new DefaultExchange(camelContext);
		
		producerTemplate.send(URI, exchange);
		
		mockHttpEndpoint.assertIsSatisfied();
		assertTrue(exchange.isFailed());
	}
	
}
