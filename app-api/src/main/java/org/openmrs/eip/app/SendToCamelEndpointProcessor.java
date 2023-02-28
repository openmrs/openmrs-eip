/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.eip.app;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Super interface for processors that send items to a camel endpoint for processing
 * 
 * @param <T> item type
 */
public interface SendToCamelEndpointProcessor<T> {
	
	Logger log = LoggerFactory.getLogger(SendToCamelEndpointProcessor.class);
	
	/**
	 * Calls the specified camel endpoint with the exchange body set to the specified item
	 * 
	 * @param endpointUri the endpoint uri to sent to
	 * @param item the item to set as the exchange body
	 * @param producerTemplate the {@link ProducerTemplate} object to use to send
	 */
	default void send(String endpointUri, T item, ProducerTemplate producerTemplate) {
		try {
			producerTemplate.sendBody(endpointUri, convertBody(item));
			onSuccess(item);
		}
		catch (Throwable t) {
			onFailure(item, t);
		}
	}
	
	/**
	 * Converts the specified item before it is sent to the endpoint, this method by default does no
	 * conversion i.e. returns the passed in item, subclasses can do more meaningful conversions.
	 *
	 * @param item the item to convert
	 * @return the converted object
	 */
	default Object convertBody(T item) {
		return item;
	}
	
	/**
	 * Subclasses can override this method to do post-processing of the item upon success, this method
	 * by default does nothing.
	 *
	 * @param item the item that was successfully processed
	 */
	default void onSuccess(T item) {
	}
	
	/**
	 * If the endpoint does not gracefully handle errors, subclasses can override this method to be to
	 * do post-processing of the item upon failure, this method by default only logs the error if debug
	 * is enabled for the logger.
	 *
	 * @param item the failed item
	 */
	default void onFailure(T item, Throwable throwable) {
		if (log.isDebugEnabled()) {
			log.error("An error occurred while sending item to camel endpoint", throwable);
		}
	}
	
}
