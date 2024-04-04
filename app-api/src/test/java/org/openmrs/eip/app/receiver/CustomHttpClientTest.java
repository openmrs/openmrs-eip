package org.openmrs.eip.app.receiver;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class CustomHttpClientTest {
	
	private static final String HOST = "http://localhost";
	
	private static final String USER = "user";
	
	private static final String PASSWORD = "pass";
	
	@Rule
	public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().dynamicPort());
	
	private CustomHttpClient client;
	
	@Before
	public void setup() {
		client = new CustomHttpClient();
		Whitebox.setInternalState(client, "baseUrl", HOST + ":" + wireMockRule.port());
		Whitebox.setInternalState(client, "username", USER);
		Whitebox.setInternalState(client, "password", PASSWORD.toCharArray());
	}
	
	@Test
	public void sendRequest_shouldSendTheRequestToTheServer() throws Exception {
		final String resource = "person";
		final String json = "{}";
		WireMock.stubFor(WireMock.post(CustomHttpClient.PATH + resource)
		        .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
		        .withBasicAuth(USER, PASSWORD).withRequestBody(WireMock.equalTo(json)).willReturn(WireMock.noContent()));
		
		client.sendRequest(resource, json);
	}
	
}
