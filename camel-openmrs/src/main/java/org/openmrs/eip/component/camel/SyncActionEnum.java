package org.openmrs.eip.component.camel;

import java.util.Arrays;

import org.apache.camel.Producer;
import org.openmrs.eip.component.exception.EIPException;

public enum SyncActionEnum {
	
	EXTRACT(OpenmrsExtractProducer.class);
	
	private Class<? extends Producer> producerClass;
	
	SyncActionEnum(final Class<? extends Producer> producerClass) {
		this.producerClass = producerClass;
	}
	
	public Class<? extends Producer> getProducerClass() {
		return producerClass;
	}
	
	public static SyncActionEnum getAction(final String actionString) {
		return Arrays.stream(values()).filter(e -> e.name().equalsIgnoreCase(actionString)).findFirst()
		        .orElseThrow(() -> new EIPException("No action found with name: " + actionString));
	}
}
