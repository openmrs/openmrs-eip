/*
 * Copyright © 2021, Ozone HIS <info@ozone-his.com>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.openmrs.eip.fhir.routes.resources;

import java.util.Collections;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Quantity;
import org.hl7.fhir.r4.model.Reference;
import org.hl7.fhir.r4.model.SupplyRequest;
import org.openmrs.eip.fhir.routes.resources.models.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SupplyProcessor implements Processor {
	
	@Override
	public void process(Exchange exchange) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			Order order = objectMapper.readValue(exchange.getIn().getBody(String.class), Order.class);
			
			SupplyRequest supplyRequest = new SupplyRequest();
			supplyRequest.setId(order.getUuid());
			//			supplyRequest.setItem(new CodeableConcept(
			//			        new Coding().setCode(order.getConcept().getUuid()).setDisplay(order.getConcept().getDisplay()))
			//			                .setText(order.getConcept().getDisplay()));
			supplyRequest.setItem(new Reference().setReference("MedicalSupply/" + order.getConcept().getUuid())
			        .setDisplay(order.getConcept().getDisplay()));
			supplyRequest.setReasonReference(Collections.singletonList(
			    new Reference().setType("Encounter").setReference("Encounter/" + order.getEncounter().getUuid())));
			supplyRequest
			        .setQuantity(new Quantity().setValue(order.getQuantity()).setCode(order.getQuantityUnits().getUuid()));
			supplyRequest.setRequester(
			    new Reference().setReference(order.getOrderer().getUuid()).setDisplay(order.getOrderer().getDisplay()));
			supplyRequest.setDeliverTo(new Reference().setReference("Patient/" + order.getPatient().getUuid())
			        .setDisplay(order.getPatient().getDisplay()));
			supplyRequest.setStatus(SupplyRequest.SupplyRequestStatus.ACTIVE);
			
			exchange.getMessage().setBody(supplyRequest);
		}
		catch (Exception e) {
			throw new CamelExecutionException("Error transforming Order to SupplyRequest", exchange, e);
		}
	}
}
