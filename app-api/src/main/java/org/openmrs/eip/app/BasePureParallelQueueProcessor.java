package org.openmrs.eip.app;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for pure parallel queue processors, a pure processor is one that can process any item
 * in parallel regardless of anything.
 *
 * @param <T> item type
 */
public abstract class BasePureParallelQueueProcessor<T extends AbstractEntity> extends BaseQueueProcessor<T> {
	
	public BasePureParallelQueueProcessor(ThreadPoolExecutor executor) {
		super(executor);
	}
	
	@Override
	public String getUniqueId(T item) {
		return item.getId().toString();
	}
	
	@Override
	public String getLogicalType(T item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
	}
	
}
