package org.openmrs.eip.app;

import static org.openmrs.eip.app.SyncConstants.DEFAULT_THREAD_NUMBER;
import static org.openmrs.eip.app.SyncConstants.PROP_THREAD_NUMBER;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spi.CamelEvent;
import org.apache.camel.support.EventNotifierSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseParallelProcessor extends EventNotifierSupport implements Processor {
	
	private static final Logger log = LoggerFactory.getLogger(BaseParallelProcessor.class);
	
	protected static final int WAIT_IN_SECONDS = 60;
	
	@Value("${" + PROP_THREAD_NUMBER + ":" + DEFAULT_THREAD_NUMBER + "}")
	protected int threadCount;
	
	protected ProducerTemplate producerTemplate;
	
	protected static ExecutorService executor;
	
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
		
		if (waitForTasksIndefinitely()) {
			allFuture.get();
		} else {
			allFuture.get(WAIT_IN_SECONDS - 30, TimeUnit.SECONDS);
		}
		
		if (log.isDebugEnabled()) {
			log.debug(futures.size() + " " + getProcessorName() + " processor thread(s) have terminated");
		}
	}
	
	@Override
	public void notify(CamelEvent event) {
		if (event instanceof CamelEvent.CamelContextStartedEvent) {
			if (executor == null) {
				executor = Executors.newFixedThreadPool(threadCount);
			}
		} else if (event instanceof CamelEvent.CamelContextStoppingEvent) {
			if (executor != null) {
				log.info("Shutting down executor for " + getProcessorName() + " processor threads");
				
				executor.shutdownNow();
				
				try {
					log.info("Waiting for " + WAIT_IN_SECONDS + " seconds for " + getProcessorName()
					        + " processor threads to terminate");
					
					executor.awaitTermination(WAIT_IN_SECONDS, TimeUnit.SECONDS);
					
					log.info("Done shutting down executor for " + getProcessorName() + " processor threads");
				}
				catch (Exception e) {
					log.error(
					    "An error occurred while waiting for " + getProcessorName() + " processor threads to terminate", e);
				}
			}
		}
	}
	
	/**
	 * Gets the logical processor name
	 * 
	 * @return the processor name
	 */
	public abstract String getProcessorName();
	
	/**
	 * Specifies if the executor should wait indefinitely for executing tasks or not
	 * 
	 * @return true or false
	 */
	public boolean waitForTasksIndefinitely() {
		return false;
	}
	
}
