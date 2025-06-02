package org.openmrs.eip.fhir.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TokenCacheTest {
	
	@Mock
	private OpenmrsFhirOauth2 openmrsFhirOauth2;
	
	@InjectMocks
	private TokenCache tokenCache;
	
	private static AutoCloseable mocksCloser;
	
	@BeforeEach
	void setUp() {
		mocksCloser = MockitoAnnotations.openMocks(this);
	}
	
	@AfterAll
	static void closeMocks() throws Exception {
		mocksCloser.close();
	}
	
	@Test
	@DisplayName("Should fetch new token when token is null.")
	void shouldFetchesNewTokenWhenTokenIsNull() {
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setExpiresIn("3600");
		tokenInfo.setAccessToken("token");
		tokenInfo.setTokenType("Bearer");
		
		when(openmrsFhirOauth2.fetchTokenInfo()).thenReturn(tokenInfo);
		
		// Act
		TokenInfo result = tokenCache.getTokenInfo();
		
		assertEquals(tokenInfo, result);
		verify(openmrsFhirOauth2, times(1)).fetchTokenInfo();
	}
	
	@Test
	@DisplayName("Should fetch new token when token is expired.")
	void shouldFetchesNewTokenWhenTokenIsExpired() {
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setTokenType("Bearer");
		tokenInfo.setAccessToken("expiredToken");
		tokenInfo.setExpiresIn(System.currentTimeMillis() - 1 + "");
		tokenCache.setTokenInfo(tokenInfo);
		
		TokenInfo newTokenInfo = new TokenInfo();
		newTokenInfo.setExpiresIn("3600");
		newTokenInfo.setAccessToken("newToken");
		newTokenInfo.setTokenType("Bearer");
		
		when(openmrsFhirOauth2.fetchTokenInfo()).thenReturn(newTokenInfo);
		
		TokenInfo result = tokenCache.getTokenInfo();
		
		assertEquals(newTokenInfo, result);
		verify(openmrsFhirOauth2, times(1)).fetchTokenInfo();
	}
	
	@Test
	@DisplayName("Should return existing token when token is valid.")
	void ShouldReturnExistingTokenWhenTokenIsValid() {
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setExpiresIn("3600");
		tokenInfo.setAccessToken("token");
		tokenInfo.setTokenType("Bearer");
		
		setField(tokenCache, "tokenInfo", tokenInfo);
		setField(tokenCache, "expiryTime", System.currentTimeMillis() + 1000);
		
		TokenInfo result = tokenCache.getTokenInfo();
		
		assertEquals(tokenInfo, result);
		verify(openmrsFhirOauth2, never()).fetchTokenInfo();
	}
	
	@Test
	@DisplayName("Should fetch new token when within clock skew window.")
	void shouldFetchNewTokenWhenWithinClockSkewWindow() {
		when(openmrsFhirOauth2.getClockSkewSeconds()).thenReturn(30);
		
		TokenInfo oldToken=new TokenInfo();
		oldToken.setExpiresIn("3600");
		oldToken.setAccessToken("oldToken");
		oldToken.setTokenType("Bearer");
		
		// Set expiry time to 20 seconds from now (within clock skew window)
		long expiryTime=System.currentTimeMillis() + 20_000;
		tokenCache.setTokenInfo(oldToken);
		tokenCache.setExpiryTime(expiryTime);
		
		TokenInfo newToken=new TokenInfo();
		newToken.setExpiresIn("3600");
		newToken.setAccessToken("newToken");
		newToken.setTokenType("Bearer");
		
		when(openmrsFhirOauth2.fetchTokenInfo()).thenReturn(newToken);
		
		TokenInfo result=tokenCache.getTokenInfo();
		
		assertEquals(newToken,result);
		// Verify that fetchTokenInfo & getClockSkewSeconds were called
		verify(openmrsFhirOauth2).fetchTokenInfo();
		verify(openmrsFhirOauth2).getClockSkewSeconds();
	}
}
