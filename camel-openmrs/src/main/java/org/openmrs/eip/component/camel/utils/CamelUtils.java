package org.openmrs.eip.component.camel.utils;

import static org.openmrs.eip.component.Constants.EX_PROP_EXCEPTION;

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
		Throwable throwable = exchange.getException();
		if (throwable == null) {
			//If a route has an error handler, the exception is swallowed, check if the error handler set it on the 
			//exchange as a property
			throwable = exchange.getProperty(EX_PROP_EXCEPTION, Throwable.class);
		}
		
		if (throwable != null) {
			throw new EIPException("An error occurred while calling endpoint: " + endpointUri, throwable);
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
