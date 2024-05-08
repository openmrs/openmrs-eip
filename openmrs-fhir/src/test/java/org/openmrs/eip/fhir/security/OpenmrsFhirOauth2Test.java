package org.openmrs.eip.fhir.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class OpenmrsFhirOauth2Test {
	
	@Mock
	private HttpClient httpClient;
	
	@InjectMocks
	private OpenmrsFhirOauth2 openmrsFhirOauth2;
	
	private static AutoCloseable mocksCloser;
	
	@BeforeEach
	void setUp() {
		mocksCloser = openMocks(this);
		setField(openmrsFhirOauth2, "oauthUri", "http://localhost:8080/oauth/token");
		setField(openmrsFhirOauth2, "clientId", "client");
		setField(openmrsFhirOauth2, "clientSecret", "secret");
		setField(openmrsFhirOauth2, "scope", "openid");
		openmrsFhirOauth2.setHttpClient(httpClient);
	}
	
	@AfterAll
	static void closeMocks() throws Exception {
		mocksCloser.close();
	}
	
	@Test
	@DisplayName("Should fetch auth token successfully.")
	@SuppressWarnings("unchecked")
	void shouldFetchTokenInfoSuccessfully() throws Exception {
		var mockResponse = mock(HttpResponse.class);
		when(mockResponse.statusCode()).thenReturn(200);
		when(mockResponse.body())
		        .thenReturn("{\"access_token\":\"token\",\"expires_in\":\"3600\",\"token_type\":\"Bearer\"}");
		when(httpClient.send(any(), any())).thenReturn(mockResponse);
		
		TokenInfo token = openmrsFhirOauth2.fetchTokenInfo();
		
		assertEquals("token", token.getAccessToken());
	}
	
	@Test
	@DisplayName("Should throw exception when response is not successful.")
	@SuppressWarnings("unchecked")
	void shouldThrowExceptionWhenResponseIsNotSuccessful() throws Exception {
		var mockResponse = mock(HttpResponse.class);
		when(mockResponse.statusCode()).thenReturn(400);
		when(httpClient.send(any(), any())).thenReturn(mockResponse);
		
		assertThrows(RuntimeException.class, () -> openmrsFhirOauth2.fetchTokenInfo());
	}
	
	@Test
	@DisplayName("Should throw exception when HttpClient throws exception.")
	void shouldThrowExceptionWhenHttpClientThrowsException() {
		openmrsFhirOauth2.setHttpClient(null);
		
		assertThrows(RuntimeException.class, () -> openmrsFhirOauth2.fetchTokenInfo());
	}
	
	@Test
	@DisplayName("Should throw exception when token type is invalid.")
	@SuppressWarnings("unchecked")
	void shouldThrowExceptionWhenTokenTypeIsInvalid() throws Exception {
		var mockResponse = mock(HttpResponse.class);
		when(mockResponse.statusCode()).thenReturn(200);
		when(mockResponse.body())
		        .thenReturn("{\"access_token\":\"token\",\"expires_in\":\"3600\",\"token_type\":\"invalid\"}");
		when(httpClient.send(any(), any())).thenReturn(mockResponse);
		
		assertThrows(RuntimeException.class, () -> openmrsFhirOauth2.fetchTokenInfo());
	}
	
	@Test
	@DisplayName("Should throw exception when required properties are missing.")
	void shouldThrowExceptionWhenRequiredPropertiesAreMissing() {
		setField(openmrsFhirOauth2, "oauthUri", null);
		
		assertThrows(IllegalStateException.class, () -> openmrsFhirOauth2.validateOAuthConfig());
	}
}
