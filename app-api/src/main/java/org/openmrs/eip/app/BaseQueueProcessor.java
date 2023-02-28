package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for processors that operate on items in a DB sync related queue and forward each item
 * to another handler camel endpoint for processing
 *
 * @param <T> item type
 */
public abstract class BaseQueueProcessor<T extends AbstractEntity> extends BaseParallelProcessor<List<T>> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseQueueProcessor.class);
	
	@Override
	public void processWork(List<T> items) throws Exception {
		if (items.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No items in the list to process");
			}
			
			return;
		}
		
		List<String> uniqueKeys = synchronizedList(new ArrayList(threadCount));
		List<CompletableFuture<Void>> syncThreadFutures = synchronizedList(new ArrayList(threadCount));
		
		for (T item : items) {
			if (AppUtils.isAppContextStopping()) {
				if (log.isDebugEnabled()) {
					log.debug("Stopping item processing because application context is stopping");
				}
				
				break;
			}
			
			final String id = getUniqueId(item);
			final String logicalType = getLogicalType(item);
			final String logicalKey = logicalType + "#" + id;
			if (uniqueKeys.contains(logicalKey)) {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(item);
					if (log.isDebugEnabled()) {
						log.debug("Postponed processing of {} because of earlier unprocessed item(s) for the same entity",
						    item);
					}
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
				
				continue;
			}
			
			List<String> typesInHierarchy = getLogicalTypeHierarchy(logicalType);
			if (typesInHierarchy == null) {
				uniqueKeys.add(logicalKey);
			} else {
				for (String type : typesInHierarchy) {
					uniqueKeys.add(type + "#" + id);
				}
			}
			
			//TODO Periodically wait and reset futures to save memory
			syncThreadFutures.add(CompletableFuture.runAsync(() -> {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(item);
					processItem(item);
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}, executor));
		}
		
		if (syncThreadFutures.size() > 0) {
			waitForFutures(syncThreadFutures);
		}
	}
	
	private void setThreadName(T item) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":" + getQueueName() + ":" + getThreadName(item));
	}
	
	/**
	 * Processes the specified item
	 *
	 * @param item the queue item to process
	 */
	public abstract void processItem(T item);
	
	/**
	 * Gets a unique identifier for the specified item
	 *
	 * @param item the item
	 * @return the key
	 */
	public abstract String getUniqueId(T item);
	
	/**
	 * Gets the logical queue name
	 *
	 * @return the logical name
	 */
	public abstract String getQueueName();
	
	/**
	 * Generate a unique name for the thread that will process the item
	 *
	 * @param item the queue item
	 * @return the thread name
	 */
	public abstract String getThreadName(T item);
	
	/**
	 * Gets logical type name of the item
	 *
	 * @param item the item
	 * @return the logical type name of the item
	 */
	public abstract String getLogicalType(T item);
	
	/**
	 * Gets the list of logical types in the same hierarchy as the specified logical type, the method
	 * should return null if the type has no hierarchy.
	 *
	 * @param logicalType logical type to match
	 * @return list of types in the same hierarchy
	 */
	public abstract List<String> getLogicalTypeHierarchy(String logicalType);
	
}
