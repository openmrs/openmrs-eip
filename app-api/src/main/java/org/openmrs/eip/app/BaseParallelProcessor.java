package org.openmrs.eip.app;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseParallelProcessor<W> {
	
	private static final Logger log = LoggerFactory.getLogger(BaseParallelProcessor.class);
	
	/**
	 * Wait for all the Future instances in the specified list to terminate
	 *
	 * @param futures the list of Futures instance to wait for
	 * @throws Exception
	 */
	public void waitForFutures(List<CompletableFuture<Void>> futures) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Waiting for " + futures.size() + " " + getProcessorName() + " processor thread(s) to terminate");
		}
		
		CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		
		allFuture.get();
		
		if (log.isDebugEnabled()) {
			log.debug(futures.size() + " " + getProcessorName() + " processor thread(s) have terminated");
		}
	}
	
	/**
	 * Processes the specified work
	 */
	public abstract void processWork(W work) throws Exception;
	
	/**
	 * Gets the logical processor name
	 * 
	 * @return the processor name
	 */
	public abstract String getProcessorName();
	
}
