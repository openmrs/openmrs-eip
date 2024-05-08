package org.openmrs.eip.fhir.security.interceptor;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.eip.fhir.security.TokenCache;
import org.openmrs.eip.fhir.security.TokenInfo;

import ca.uhn.fhir.rest.client.api.IHttpRequest;

class Oauth2InterceptorTest {
	
	@Mock
	private TokenCache tokenCache;
	
	@Mock
	private IHttpRequest iHttpRequest;
	
	@InjectMocks
	private Oauth2Interceptor oauth2Interceptor;
	
	private static AutoCloseable mocksCloser;
	
	@BeforeEach
	void setUp() {
		mocksCloser = openMocks(this);
	}
	
	@AfterAll
	static void closeMocks() throws Exception {
		mocksCloser.close();
	}
	
	@Test
	void shouldAddAuthorizationHeaderWithBearerToken() {
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setAccessToken("testToken");
		when(tokenCache.getTokenInfo()).thenReturn(tokenInfo);
		
		oauth2Interceptor.interceptRequest(iHttpRequest);
		
		verify(iHttpRequest).addHeader("Authorization", "Bearer " + tokenInfo.getAccessToken());
	}
}
