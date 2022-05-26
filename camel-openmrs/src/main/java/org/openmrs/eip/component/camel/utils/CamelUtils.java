package org.openmrs.eip.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.exception.EIPException;

public class CamelUtils {
	
	/**
	 * Calls the specified endpoint with the specified {@link Exchange} and {@link ProducerTemplate} instances
	 * 
	 * @param endpointUri the uri to call
	 * @param exchange The {@link Exchange} object
	 * @param template The {@link ProducerTemplate} object
	 * @return the {@link Exchange} object
	 */
	public static Exchange send(String endpointUri, Exchange exchange, ProducerTemplate template) {
		exchange = template.send(endpointUri, exchange);
		if (exchange.getException() != null) {
			exchange.getException().printStackTrace();
			throw new EIPException("An error occurred while calling endpoint: " + endpointUri);
		}
		
		return exchange;
	}
	
}
