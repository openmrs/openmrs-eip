package org.openmrs.eip.fhir.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmrs.eip.OauthToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Base64;

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
	
	private HttpClient httpClient = HttpClient.newBuilder().build();
	
	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	public OauthToken fetchAuthToken() {
		// Prepare credentials
		String credentials = clientId + ":" + clientSecret;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		
		// Prepare request body
		String requestBody = "grant_type=client_credentials&scope=" + scope;
		
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(oauthUri))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.header("Authorization", "Basic " + encodedCredentials)
				.POST(HttpRequest.BodyPublishers.ofString(requestBody)).build();
		try {
			HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
			if (response.statusCode() == 200) {
				ObjectMapper mapper = new ObjectMapper();
				AccessToken accessToken = mapper.readValue(response.body(), AccessToken.class);
				return new OauthToken(accessToken.getAccessToken(), accessToken.getExpiresAt());
			} else {
				throw new RuntimeException("Failed to retrieve token: " + response.body());
			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to retrieve token", e);
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class AccessToken {
		
		@JsonProperty("access_token")
		private String accessToken;
		
		@JsonProperty("expires_in")
		private String expiresIn;
		
		public String getAccessToken() {
			return accessToken;
		}
		
		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
		
		public Long getExpiresAt() {
			return (Long.parseLong(expiresIn) + LocalDateTime.now().getSecond()) - 10;
		}
		
		public String getExpiresIn() {
			return expiresIn;
		}
		
		public void setExpiresIn(String expiresIn) {
			this.expiresIn = expiresIn;
		}
	}
}
