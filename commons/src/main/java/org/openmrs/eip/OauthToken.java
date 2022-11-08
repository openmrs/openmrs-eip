package org.openmrs.eip;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Encapsulates data for an auth token
 */
public class OauthToken {
	
	private String accessToken;
	
	private LocalDateTime expiryDatetime;
	
	/**
	 * Subtracts 10s so that the epiration happens 10s before actual expiration
	 *
	 * @param accessToken the oauth access token
	 * @param expiresAt seconds since the epoch when the token will expire
	 */
	public OauthToken(String accessToken, long expiresAt) {
		this.accessToken = accessToken;
		expiryDatetime = Instant.ofEpochSecond(expiresAt).atZone(ZoneId.systemDefault()).toLocalDateTime().minusSeconds(10);
	}
	
	/**
	 * Gets the accessToken
	 *
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}
	
	/**
	 * Returns true if the token has expired otherwise false
	 * 
	 * @return true or false
	 */
	public boolean isExpired(LocalDateTime asOfDatetime) {
		return asOfDatetime.compareTo(expiryDatetime) > -1;
	}
	
}
