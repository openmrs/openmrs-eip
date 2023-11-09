package org.openmrs.eip.camel;

import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneId.systemDefault;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.openmrs.eip.camel.OauthProcessor.HTTP_AUTH_SCHEME;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.OauthToken;
import org.openmrs.eip.Utils;

public class OauthProcessorTest {
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private CamelContext mockCamelContext;
	
	@Mock
	private OauthToken mockOauthToken;
	
	private OauthProcessor processor;
	
	private AutoCloseable openMocksAutoCloseable;
	
	private AutoCloseable mockStaticAutoCloseable;
	
	@BeforeEach
	public void setup() throws Exception {
		this.openMocksAutoCloseable = openMocks(this);
		this.mockStaticAutoCloseable = mockStatic(Utils.class);
		
		processor = new OauthProcessor();
		setInternalState(processor, "isOauthEnabled", false);
		setInternalState(processor, "producerTemplate", mockProducerTemplate);
	}
	
	@AfterEach
	public void tearDown() throws Exception {
		this.openMocksAutoCloseable.close();
		this.mockStaticAutoCloseable.close();
	}
	
	@Test
	public void process_shouldSkipSettingTheOauthHeaderIfDisabled() throws Exception {
		processor.process(new DefaultExchange((CamelContext) mockCamelContext));
		
		verifyNoInteractions(mockProducerTemplate);
	}
	
	@Test
	public void process_shouldReturnTheCachedTokenIfItIsNotExpired() throws Exception {
		final String testToken = "testToken";
		when(mockOauthToken.getAccessToken()).thenReturn(testToken);
		when(mockOauthToken.isExpired(any(LocalDateTime.class))).thenReturn(false);
		setInternalState(processor, "oauthToken", mockOauthToken);
		setInternalState(processor, "isOauthEnabled", true);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		verifyNoInteractions(mockProducerTemplate);
		assertEquals(HTTP_AUTH_SCHEME + " " + testToken, exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldGetNewTokenAndSetTheHeaderIfEnabledAndThereIsNoCachedToken() throws Exception {
		final String expectedToken = "some-token";
		final long expiresIn = 300;
		final long testSeconds = 1626898515;
		setInternalState(processor, "isOauthEnabled", true);
		Map<String, Object> testResponse = new HashMap<>();
		testResponse.put(OauthProcessor.FIELD_TOKEN, expectedToken);
		testResponse.put(OauthProcessor.FIELD_TYPE, HTTP_AUTH_SCHEME);
		testResponse.put(OauthProcessor.FIELD_EXPIRES_IN, expiresIn);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		assertNull(getInternalState(processor, "oauthToken"));
		when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		OauthToken cachedOauthToken = getInternalState(processor, "oauthToken");
		assertNotNull(cachedOauthToken);
		assertEquals(expectedToken, cachedOauthToken.getAccessToken());
		LocalDateTime testLocalDt = ofEpochSecond(testSeconds + expiresIn - 10).atZone(systemDefault()).toLocalDateTime();
		assertEquals(testLocalDt, getInternalState(cachedOauthToken, "expiryDatetime"));
		assertEquals(HTTP_AUTH_SCHEME + " " + expectedToken, exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldCallGetNewTokenAndSetTheHeaderIfEnabledAndTheCachedTokenIsExpired() throws Exception {
		when(mockOauthToken.getAccessToken()).thenReturn("am-expired");
		when(mockOauthToken.isExpired(any(LocalDateTime.class))).thenReturn(true);
		setInternalState(processor, "oauthToken", mockOauthToken);
		
		final String expectedNewToken = "some-token-1";
		final long expiresIn = 360;
		final long testSeconds = 1626898515;
		setInternalState(processor, "isOauthEnabled", true);
		Map<String, Object> testResponse = new HashMap<>();
		testResponse.put(OauthProcessor.FIELD_TOKEN, expectedNewToken);
		testResponse.put(OauthProcessor.FIELD_TYPE, HTTP_AUTH_SCHEME);
		testResponse.put(OauthProcessor.FIELD_EXPIRES_IN, expiresIn);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		setInternalState(processor, "oauthToken", mockOauthToken);
		when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		// Verify
		OauthToken newCachedOauthToken = getInternalState(processor, "oauthToken");
		assertNotNull(newCachedOauthToken);
		assertEquals(expectedNewToken, newCachedOauthToken.getAccessToken());
		LocalDateTime testLocalDt = ofEpochSecond(testSeconds + expiresIn - 10).atZone(systemDefault()).toLocalDateTime();
		assertEquals(testLocalDt, getInternalState(newCachedOauthToken, "expiryDatetime"));
		assertEquals(HTTP_AUTH_SCHEME + " " + expectedNewToken, exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldFailWhenTheReturnedTokenHasAnUnSupportedType() throws Exception {
		setInternalState(processor, "isOauthEnabled", true);
		Map<String, Object> testResponse = new HashMap<>();
		testResponse.put(OauthProcessor.FIELD_TOKEN, "some-token");
		final String type = "MAC";
		testResponse.put(OauthProcessor.FIELD_TYPE, type);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		assertNull(getInternalState(processor, "oauthToken"));
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		assertThrows(EIPException.class, () -> {
			try {
				processor.process(exchange);
			}
			catch (Exception e) {
				throw new EIPException("Unsupported oauth token type: " + type, e);
			}
		}, "Unsupported oauth token type: " + type);
	}
	
	@Test
	public void process_shouldSetBodyToNullIfOauthIsDisabled() throws Exception {
		setInternalState(processor, "isOauthEnabled", false);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		exchange.getIn().setBody("Testing");
		
		processor.process(exchange);
		
		assertNull(exchange.getIn().getBody());
	}
}
