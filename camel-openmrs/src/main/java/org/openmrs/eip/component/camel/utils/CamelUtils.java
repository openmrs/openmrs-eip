package org.openmrs.eip.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;

public class CamelUtils {
	
	private static ProducerTemplate producerTemplate;
	
	/**
	 * Calls the specified endpoint with the specified {@link Exchange}
	 * 
	 * @param endpointUri the uri to call
	 * @param exchange The {@link Exchange} object
	 * @return the {@link Exchange} object
	 */
	public static Exchange send(String endpointUri, Exchange exchange) {
		exchange = getProducerTemplate().send(endpointUri, exchange);
		if (exchange.getException() != null) {
			throw new EIPException("An error occurred while calling endpoint: " + endpointUri, exchange.getException());
		}
		
		return exchange;
	}
	
	/**
	 * Calls the specified endpoint
	 *
	 * @param endpointUri the uri to call
	 */
	public static void send(String endpointUri) {
		send(endpointUri, ExchangeBuilder.anExchange(getProducerTemplate().getCamelContext()).build());
	}
	
	private static ProducerTemplate getProducerTemplate() {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		return producerTemplate;
	}
	
}
