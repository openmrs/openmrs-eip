package org.openmrs.eip.fhir.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("tokenCache")
public class TokenCache {
	
	@Autowired
	private OpenmrsFhirOauth2 openmrsFhirOauth2;
	
	private TokenInfo tokenInfo;
	
	private long expiryTime;
	
	public TokenCache() {
	}
	
	public void setTokenInfo(TokenInfo tokenInfo) {
		this.tokenInfo = tokenInfo;
	}
	
	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}
	
	/**
	 * Fetches the token info from the Oauth server if it is not already cached or if it has expired
	 *
	 * @return the token info
	 */
	public TokenInfo getTokenInfo() {
		if (tokenInfo == null || shouldRefreshToken()) {
			this.tokenInfo = openmrsFhirOauth2.fetchTokenInfo();
			this.setExpiryTime(calculateExpiryTime());
		}
		return tokenInfo;
	}
	
	/**
	 * Checks if the token should be refreshed based on expiry time and clock skew
	 *
	 * @return true if the token should be refreshed
	 */
	boolean shouldRefreshToken() {
		return System.currentTimeMillis() > (expiryTime - (openmrsFhirOauth2.getClockSkewSeconds() * 1000L));
	}
	
	private long calculateExpiryTime() {
		return System.currentTimeMillis() + (Long.parseLong(tokenInfo.getExpiresIn()) * 1000);
	}
}
