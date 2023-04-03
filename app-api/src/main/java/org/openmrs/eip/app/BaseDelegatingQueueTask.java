package org.openmrs.eip.app;

import java.util.List;

import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for queue tasks that delegates to a queue processor
 */
public abstract class BaseDelegatingQueueTask<T extends AbstractEntity, P extends BaseQueueProcessor<T>> extends BaseQueueTask<T> {
	
	private P processor;
	
	public BaseDelegatingQueueTask(P processor) {
		this.processor = processor;
	}
	
	@Override
	public void process(List<T> items) throws Exception {
		processor.processWork(items);
	}
	
}
