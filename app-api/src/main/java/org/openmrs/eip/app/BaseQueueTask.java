package org.openmrs.eip.app;

import java.util.List;

import org.openmrs.eip.app.management.entity.AbstractEntity;

/**
 * Base class for tasks that read a batch of items from a database queue table and processes them.
 *
 * @param <T> the queue entity type
 */
public abstract class BaseQueueTask<T extends AbstractEntity> extends BaseTask {
	
	@Override
	public boolean doRun() throws Exception {
		List<T> items = getNextBatch();
		if (items.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No items found to process");
			}
			
			return true;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Processing " + items.size() + " items(s)");
		}
		
		process(items);
		
		return false;
	}
	
	/**
	 * Gets the next batch of items to process
	 *
	 * @return List of items
	 */
	public abstract List<T> getNextBatch();
	
	/**
	 * Processes the specified list of items
	 *
	 * @param items list of items
	 * @throws Exception
	 */
	public abstract void process(List<T> items) throws Exception;
	
}
