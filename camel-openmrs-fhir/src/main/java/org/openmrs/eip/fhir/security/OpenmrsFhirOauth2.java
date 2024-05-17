package org.openmrs.eip.fhir.security;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.openmrs.eip.EIPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OpenmrsFhirOauth2 {
	
	@Value("${oauth.access.token.uri}")
	private String oauthUri;
	
	@Value("${oauth.client.secret}")
	private String clientSecret;
	
	@Value("${oauth.client.id}")
	private String clientId;
	
	@Value("${oauth.client.scope}")
	private String scope;
	
	public static final String HTTP_AUTH_SCHEME = "Bearer";
	
	private HttpClient httpClient = HttpClient.newBuilder().build();
	
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	/**
	 * Fetches the OAuth2 token from the configured OAuth2 server.
	 *
	 * @return the token info
	 */
	public TokenInfo fetchTokenInfo() {
		this.validateOAuthConfig();
		try {
			HttpResponse<String> response = httpClient.send(this.buildRequest(), HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				return this.parseToken(response.body());
			} else {
				throw new RuntimeException("Failed to retrieve token: " + response.body());
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to retrieve token", e);
		}
	}
	
	/**
	 * Builds the request to fetch the OAuth2 token.
	 *
	 * @return the request
	 */
	private HttpRequest buildRequest() {
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		String requestBody = "grant_type=client_credentials&scope=" + scope;
		
		return HttpRequest.newBuilder().uri(URI.create(oauthUri)).header("Content-Type", "application/x-www-form-urlencoded")
		        .header("Authorization", "Basic " + encodedCredentials)
		        .POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
	}
	
	/**
	 * Validates the OAuth2 configuration.
	 */
	protected void validateOAuthConfig() {
		if (oauthUri == null || clientSecret == null || clientId == null || scope == null) {
			throw new IllegalStateException("Missing required properties for OAuth2 configuration.");
		}
	}
	
	/**
	 * Parses the token from the response body.
	 *
	 * @param responseBody the response body
	 * @return the token info
	 */
	private TokenInfo parseToken(String responseBody) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			TokenInfo tokenInfo = mapper.readValue(responseBody, TokenInfo.class);
			this.validateTokenType(tokenInfo);
			return tokenInfo;
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to parse token", e);
		}
	}
	
	/**
	 * Validates the token type. Only Bearer tokens are supported.
	 *
	 * @param tokenInfo the token info
	 */
	private void validateTokenType(TokenInfo tokenInfo) {
		if (!tokenInfo.getTokenType().equalsIgnoreCase(HTTP_AUTH_SCHEME)) {
			throw new EIPException("Invalid token type: " + tokenInfo.getTokenType());
		}
	}
}
