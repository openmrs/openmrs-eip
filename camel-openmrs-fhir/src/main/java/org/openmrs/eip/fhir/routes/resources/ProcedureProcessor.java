/*
 * Copyright © 2021, Ozone HIS <info@ozone-his.com>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmrs.eip.fhir.routes.resources;

import static org.openmrs.eip.fhir.Constants.HEADER_FHIR_EVENT_TYPE;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.ServiceRequest;
import org.hl7.fhir.r4.model.SupplyRequest;
import org.openmrs.eip.fhir.routes.resources.models.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProcedureProcessor implements Processor {
	
	@Override
	public void process(Exchange exchange) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Order order = objectMapper.readValue(exchange.getIn().getBody(String.class), Order.class);
			
			SupplyRequest supplyRequest = new SupplyRequest();
			
			exchange.getMessage().setBody(supplyRequest);
		}
		catch (Exception e) {
			throw new CamelExecutionException("Error processing ServiceRequest", exchange, e);
		}
	}
}
