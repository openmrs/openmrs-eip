/*
 * Copyright (C) Amiyul LLC - All Rights Reserved
 *
 * This source code is protected under international copyright law. All rights
 * reserved and protected by the copyright holder.
 *
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holder. If you encounter this file and do not have
 * permission, please contact the copyright holder and delete this file.
 */
package org.openmrs.eip.camel;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.openmrs.eip.EIPException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * An authenticator that uses an HTTP client to authenticate the client with the provider, this is
 * the preferred choice over the {@link CamelOauthAuthenticator} if the client needs to be
 * authenticated before the spring and camel contexts are created.
 */
public class HttpOauthAuthenticator implements OauthAuthenticator {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private String tokenUrl;
	
	private String clientId;
	
	private char[] clientSecret;
	
	private String scope;
	
	private HttpClient client;
	
	public HttpOauthAuthenticator(String tokenUrl, String clientId, char[] clientSecret, String scope) {
		this.tokenUrl = tokenUrl;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.scope = scope;
	}
	
	@Override
	public Map<String, Object> authenticate() throws Exception {
		Builder reqBuilder = HttpRequest.newBuilder();
		reqBuilder.uri(URI.create(tokenUrl)).setHeader("Content-Type", "application/x-www-form-urlencoded");
		final String body = "grant_type=client_credentials&client_id=" + clientId + "&client_secret="
		        + new String(clientSecret) + "&scope=" + scope;
		reqBuilder.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8));
		HttpResponse<byte[]> response = getClient().send(reqBuilder.build(), BodyHandlers.ofByteArray());
		if (response.statusCode() != 200) {
			throw new EIPException("Failed to retrieve OAuth token, response status code: " + response.statusCode());
		}
		
		return MAPPER.readValue(response.body(), Map.class);
	}
	
	private HttpClient getClient() {
		if (client == null) {
			synchronized (this) {
				if (client == null) {
					client = HttpClient.newHttpClient();
				}
			}
		}
		
		return client;
	}
	
}
