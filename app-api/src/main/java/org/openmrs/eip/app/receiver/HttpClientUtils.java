package org.openmrs.eip.app.receiver;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Contains utilities for an {@link HttpClient}
 */
public class HttpClientUtils {
	
	/**
	 * @see HttpClient#send(HttpRequest, HttpResponse.BodyHandler)
	 */
	protected static <T> HttpResponse<T> send(HttpClient client, HttpRequest request,
	                                          HttpResponse.BodyHandler<T> responseBodyHandler)
	    throws IOException, InterruptedException {
		return client.send(request, responseBodyHandler);
	}
	
}
