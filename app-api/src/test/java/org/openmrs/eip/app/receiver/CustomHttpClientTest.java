package org.openmrs.eip.app.receiver;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.openmrs.eip.app.receiver.CustomHttpClient.BODY_HANDLER;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.openmrs.eip.Holder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpClientUtils.class)
public class CustomHttpClientTest {
	
	private static final String BASE_URL = "http://test";
	
	private static final String USER = "user";
	
	private static final String PASSWORD = "pass";
	
	@Mock
	private HttpClient mockHttpClient;
	
	@Mock
	private HttpResponse mockResponse;
	
	private CustomHttpClient client;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(HttpClientUtils.class);
		client = new CustomHttpClient();
		Whitebox.setInternalState(client, "baseUrl", BASE_URL);
		Whitebox.setInternalState(client, "username", USER);
		Whitebox.setInternalState(client, "password", PASSWORD.toCharArray());
		Whitebox.setInternalState(client, "client", mockHttpClient);
	}
	
	@Test
	public void sendRequest_shouldSendTheRequestToTheServer() throws Exception {
		final String resource = "person";
		final String json = "{}";
		Mockito.when(mockResponse.statusCode()).thenReturn(HttpStatus.NO_CONTENT.value());
		Holder<HttpRequest> reqHolder = new Holder<>();
		PowerMockito.doAnswer((Answer) i -> {
			reqHolder.value = i.getArgument(1);
			return mockResponse;
		}).when(HttpClientUtils.class);
		HttpClientUtils.send(eq(mockHttpClient), any(HttpRequest.class), eq(BODY_HANDLER));
		
		client.sendRequest(resource, json);
		
		HttpRequest request = reqHolder.value;
		Assert.assertEquals("POST", request.method());
		Assert.assertEquals(BASE_URL + CustomHttpClient.PATH + resource, request.uri().toString());
		byte[] auth = Base64.getEncoder().encode((USER + ":" + PASSWORD).getBytes(UTF_8));
		Assert.assertEquals("Basic " + new String(auth), request.headers().firstValue(HttpHeaders.AUTHORIZATION).get());
		Assert.assertEquals(MediaType.APPLICATION_JSON_VALUE, request.headers().firstValue(HttpHeaders.CONTENT_TYPE).get());
		Assert.assertTrue(request.bodyPublisher().isPresent());
		Assert.assertEquals(json.length(), request.bodyPublisher().get().contentLength());
	}
	
}
