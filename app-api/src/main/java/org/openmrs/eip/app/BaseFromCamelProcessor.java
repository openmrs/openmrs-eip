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

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Superclass for queue processors that process a list of items read from a camel exchange body.
 * 
 * @param <T> type of the list items
 */
public abstract class BaseFromCamelProcessor<T extends AbstractEntity> extends BaseQueueProcessor<T> implements Processor {
	
	@Override
	public void process(Exchange exchange) throws Exception {
		processWork(exchange.getIn().getBody(List.class));
	}
	
}
