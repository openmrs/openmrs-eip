package org.openmrs.eip.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;

public class CamelUtils {
	
	/**
	 * Calls the specified endpoint with the specified {@link Exchange}
	 * 
	 * @param endpointUri the uri to call
	 * @param exchange The {@link Exchange} object
	 * @return the {@link Exchange} object
	 */
	public static Exchange send(String endpointUri, Exchange exchange) {
		exchange = SyncContext.getBean(ProducerTemplate.class).send(endpointUri, exchange);
		if (exchange.getException() != null) {
			throw new EIPException("An error occurred while calling endpoint: " + endpointUri, exchange.getException());
		}
		
		return exchange;
	}
	
}
