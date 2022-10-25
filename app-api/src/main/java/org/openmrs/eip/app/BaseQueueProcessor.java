package org.openmrs.eip.app;

import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.AbstractEntity;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for processors that operate on items in a DB sync related queue and forward each item
 * to another handler camel endpoint for processing
 * 
 * @param <T>
 */
public abstract class BaseQueueProcessor<T extends AbstractEntity> extends BaseParallelProcessor {
	
	private static final Logger log = LoggerFactory.getLogger(BaseQueueProcessor.class);
	
	@Override
	public void process(Exchange exchange) throws Exception {
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		List<String> uniqueKeys = synchronizedList(new ArrayList(threadCount));
		List<CompletableFuture<Void>> syncThreadFutures = synchronizedList(new ArrayList(threadCount));
		List<T> items = exchange.getIn().getBody(List.class);
		
		for (T item : items) {
			if (AppUtils.isAppContextStopping()) {
				if (log.isDebugEnabled()) {
					log.debug("Stopping item processing because application context is stopping");
				}
				
				break;
			}
			
			final String key = getItemKey(item);
			if (processInParallel(item) && !uniqueKeys.contains(key)) {
				uniqueKeys.add(key);
				
				//TODO Periodically wait and reset futures to save memory
				syncThreadFutures.add(CompletableFuture.runAsync(() -> {
					final String originalThreadName = Thread.currentThread().getName();
					try {
						setThreadName(item);
						producerTemplate.sendBody(getDestinationUri(), item);
					}
					finally {
						Thread.currentThread().setName(originalThreadName);
					}
				}, executor));
			} else {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(item);
					if (syncThreadFutures.size() > 0) {
						waitForFutures(syncThreadFutures);
						syncThreadFutures.clear();
					}
					
					producerTemplate.sendBody(getDestinationUri(), item);
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
			}
		}
		
		if (syncThreadFutures.size() > 0) {
			waitForFutures(syncThreadFutures);
		}
	}
	
	private void setThreadName(T item) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":" + getQueueName() + ":" + getThreadName(item));
	}
	
	/**
	 * Generate a unique key for the item
	 * 
	 * @param item the queue item
	 * @return the key
	 */
	public abstract String getItemKey(T item);
	
	/**
	 * Checks if the item should be processed in parallel or not
	 * 
	 * @param item the queue item
	 * @return true to process in parallel otherwise false
	 */
	public abstract boolean processInParallel(T item);
	
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
	 * Gets the camel URI to call to process a single item
	 * 
	 * @return the camel URI
	 */
	public abstract String getDestinationUri();
	
}
