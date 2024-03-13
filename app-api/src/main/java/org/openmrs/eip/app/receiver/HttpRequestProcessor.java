package org.openmrs.eip.app.receiver;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super interface for post action processors that clear cache or update search index in OpenMRS
 * 
 * @param <T>
 */
public interface HttpRequestProcessor<T> {
	
	Logger LOG = LoggerFactory.getLogger(HttpRequestProcessor.class);
	
	String CACHE_RESOURCE = "cleardbcache";
	
	String INDEX_RESOURCE = "searchindexupdate";
	
	/**
	 * Sends http request to OpenMRS
	 *
	 * @param resource the resource name
	 * @param item the item to send
	 * @param client the {@link CustomHttpClient} object
	 */
	default void sendRequest(String resource, T item, CustomHttpClient client) {
		Object converted = convertBody(item);
		if (!Collection.class.isAssignableFrom(converted.getClass())) {
			doSendRequest(client, resource, converted.toString());
		} else {
			for (Object object : (Collection) converted) {
				doSendRequest(client, resource, object.toString());
			}
		}
	}
	
	default void doSendRequest(CustomHttpClient client, String resource, String payload) {
		if (LOG.isDebugEnabled()) {
			if (resource.equals(CACHE_RESOURCE)) {
				LOG.debug("Removing the entity from OpenMRS DB cache -> {}", payload);
			} else if (resource.equals(INDEX_RESOURCE)) {
				LOG.debug("Rebuilding search Index for -> {}", payload);
			}
		}
		
		client.sendRequest(resource, payload);
	}
	
	/**
	 * Converts the specified item before it is sent.
	 *
	 * @param item the item to convert
	 * @return the converted object
	 */
	Object convertBody(T item);
	
}
