package org.openmrs.eip.camel;

import static java.time.Instant.ofEpochSecond;
import static java.time.ZoneId.systemDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.camel.OauthProcessor.HTTP_AUTH_SCHEME;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.reflect.Whitebox.getInternalState;
import static org.powermock.reflect.Whitebox.setInternalState;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.ExtendedCamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.OauthToken;
import org.openmrs.eip.Utils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Utils.class)
public class OauthProcessorTest {
	
	@Mock
	private ProducerTemplate mockProducerTemplate;
	
	@Mock
	private ExtendedCamelContext mockCamelContext;
	
	@Mock
	private OauthToken mockOauthToken;
	
	private OauthProcessor processor = new OauthProcessor();
	
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		setInternalState(processor, "producerTemplate", mockProducerTemplate);
	}
	
	@Test
	public void process_shouldSkipSettingTheOauthHeaderIfDisabled() throws Exception {
		processor.process(new DefaultExchange(mockCamelContext));
		
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
		Map<String, Object> testResponse = new HashMap();
		testResponse.put(OauthProcessor.FIELD_TOKEN, expectedToken);
		testResponse.put(OauthProcessor.FIELD_TYPE, HTTP_AUTH_SCHEME);
		testResponse.put(OauthProcessor.FIELD_EXPIRES_IN, expiresIn);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		assertNull(getInternalState(processor, "oauthToken"));
		mockStatic(Utils.class);
		PowerMockito.when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
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
		Map<String, Object> testResponse = new HashMap();
		testResponse.put(OauthProcessor.FIELD_TOKEN, expectedNewToken);
		testResponse.put(OauthProcessor.FIELD_TYPE, HTTP_AUTH_SCHEME);
		testResponse.put(OauthProcessor.FIELD_EXPIRES_IN, expiresIn);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		setInternalState(processor, "oauthToken", mockOauthToken);
		mockStatic(Utils.class);
		PowerMockito.when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		OauthToken newCachedOauthToken = getInternalState(processor, "oauthToken");
		assertNotNull(newCachedOauthToken);
		assertEquals(expectedNewToken, newCachedOauthToken.getAccessToken());
		LocalDateTime testLocalDt = ofEpochSecond(testSeconds + expiresIn - 10).atZone(systemDefault()).toLocalDateTime();
		assertEquals(testLocalDt, getInternalState(newCachedOauthToken, "expiryDatetime"));
		assertEquals(HTTP_AUTH_SCHEME + " " + expectedNewToken, exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldCallGetNewTokenAndSetTheHeaderIfEnabledAndTheCachedTokenExpiresIn10seconds() throws Exception {
		OauthToken token = new OauthToken("some-token-about-to-expire", LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset()) - 9);
		setInternalState(processor, "oauthToken", token);
		
		final String expectedNewToken = "new-token";
		final long expiresIn = 360;
		final long testSeconds = 1626898515;
		setInternalState(processor, "isOauthEnabled", true);
		Map<String, Object> testResponse = new HashMap();
		testResponse.put(OauthProcessor.FIELD_TOKEN, expectedNewToken);
		testResponse.put(OauthProcessor.FIELD_TYPE, HTTP_AUTH_SCHEME);
		testResponse.put(OauthProcessor.FIELD_EXPIRES_IN, expiresIn);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		mockStatic(Utils.class);
		PowerMockito.when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		OauthToken newCachedOauthToken = getInternalState(processor, "oauthToken");
		assertNotNull(newCachedOauthToken);
		assertEquals(expectedNewToken, newCachedOauthToken.getAccessToken());
		LocalDateTime testLocalDt = ofEpochSecond(testSeconds + expiresIn - 10).atZone(systemDefault()).toLocalDateTime();
		assertEquals(testLocalDt, getInternalState(newCachedOauthToken, "expiryDatetime"));
		assertEquals(HTTP_AUTH_SCHEME + " " + expectedNewToken, exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldNotCallGetNewTokenIfEnabledAndTheCachedTokenExpiresInMoreThan10seconds() throws Exception {
		final long testSeconds = LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset()) + 11;
		OauthToken token = new OauthToken("some-token", LocalDateTime.now().toEpochSecond(OffsetDateTime.now().getOffset()) + 11);
		
		setInternalState(processor, "oauthToken", token);
		setInternalState(processor, "isOauthEnabled", true);
		
		mockStatic(Utils.class);
		PowerMockito.when(Utils.getCurrentSeconds()).thenReturn(testSeconds);
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		processor.process(exchange);
		
		OauthToken cachedOauthToken = getInternalState(processor, "oauthToken");
		assertNotNull(cachedOauthToken);
		assertEquals(token.getAccessToken(), cachedOauthToken.getAccessToken());
		LocalDateTime testLocalDt = ofEpochSecond(testSeconds).atZone(systemDefault()).toLocalDateTime();
		assertEquals(testLocalDt, getInternalState(cachedOauthToken, "expiryDatetime"));
		assertEquals(HTTP_AUTH_SCHEME + " " + token.getAccessToken(), exchange.getIn().getBody());
	}
	
	@Test
	public void process_shouldFailWhenTheReturnedTokenHasAnUnSupportedType() throws Exception {
		setInternalState(processor, "isOauthEnabled", true);
		Map<String, Object> testResponse = new HashMap();
		testResponse.put(OauthProcessor.FIELD_TOKEN, "some-token");
		final String type = "MAC";
		testResponse.put(OauthProcessor.FIELD_TYPE, type);
		when(mockProducerTemplate.requestBody(OauthProcessor.OAUTH_URI, null, Map.class)).thenReturn(testResponse);
		assertNull(getInternalState(processor, "oauthToken"));
		Exchange exchange = new DefaultExchange(mockCamelContext);
		
		assertThrows("Unsupported oauth token type: " + type, EIPException.class, () -> processor.process(exchange));
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
