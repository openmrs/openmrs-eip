package org.openmrs.eip.app;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Superclass for queue processors that process a list of items read from a camel exchange body.
 * 
 * @param <T> type of the list items
 */
public abstract class BaseFromCamelProcessor<T extends AbstractEntity> extends BaseQueueProcessor<T> implements Processor {
	
	public BaseFromCamelProcessor(ThreadPoolExecutor executor) {
		super(executor);
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		processWork(exchange.getIn().getBody(List.class));
	}
	
}
