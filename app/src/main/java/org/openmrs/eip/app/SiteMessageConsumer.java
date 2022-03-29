package org.openmrs.eip.app;

import static java.util.Collections.singletonMap;
import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.ROUTE_URI_SYNC_PROCESSOR;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jpa.JpaConstants;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class consumes sync messages for a single site and forwards them to the
 * message processor route
 */
public class SiteMessageConsumer implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SiteMessageConsumer.class);
	
	private static final String PARAM_SITE = "site";
	
	protected static final String ENTITY = SyncMessage.class.getSimpleName();
	
	//Order by dateCreated may be just in case the DB is migrated and id change
	private static final String GET_JPA_URI = "jpa:" + ENTITY + "?query=SELECT m FROM " + ENTITY + " m WHERE m.site = :"
	        + PARAM_SITE + " ORDER BY m.id ASC &maximumResults=" + MAX_COUNT;
	
	private SiteInfo site;
	
	private boolean errorEncountered = false;
	
	private ProducerTemplate producerTemplate;
	
	private int threadCount;
	
	/**
	 * @param site sync messages from this site will be consumed by this instance
	 * @param threadCount the number of threads to use to sync messages in parallel
	 */
	public SiteMessageConsumer(SiteInfo site, int threadCount) {
		this.site = site;
		this.threadCount = threadCount;
	}
	
	@Override
	public void run() {
		producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		
		do {
			Thread.currentThread().setName(site.getIdentifier());
			
			if (log.isDebugEnabled()) {
				log.debug("Fetching next batch of messages to sync for site: " + site);
			}
			
			try {
				List<SyncMessage> syncMessages = producerTemplate.requestBodyAndHeader(GET_JPA_URI, null,
				    JpaConstants.JPA_PARAMETERS_HEADER, singletonMap(PARAM_SITE, site), List.class);
				
				if (syncMessages.isEmpty()) {
					if (log.isDebugEnabled()) {
						log.debug("No sync message found from site: " + site);
					}
					
					//TODO Make the delay configurable
					try {
						Thread.sleep(WAIT_IN_SECONDS * 1000);
					}
					catch (InterruptedException e) {
						//ignore
						log.info("Sync message consumer for site: " + site + " has been interrupted");
					}
					
					continue;
				}
				
				processMessages(syncMessages);
				
			}
			catch (Throwable t) {
				//TODO After a certain failure count may be we should shutdown the application
				//TODO Even better, add a retry mechanism for a number of times before giving up
				if (!ReceiverContext.isStopSignalReceived()) {
					log.error("Stopping message consumer thread for site: " + site + " because an error occurred", t);
					
					errorEncountered = true;
					break;
				}
			}
			
		} while (!ReceiverContext.isStopSignalReceived() && !errorEncountered);
		
		log.info("Sync message consumer for site: " + site + " has stopped");
		
	}
	
	protected void processMessages(List<SyncMessage> syncMessages) throws Exception {
		log.info("Processing " + syncMessages.size() + " message(s) from site: " + site);
		ExecutorService syncMsgExecutor = null;
		List<CompletableFuture<Void>> syncThreadFutures = synchronizedList(new ArrayList(threadCount));
		
		try {
			for (SyncMessage msg : syncMessages) {
				if (ReceiverContext.isStopSignalReceived()) {
					log.info("Sync message consumer for site: " + site + " has detected a stop signal");
					break;
				}
				
				if (msg.getSnapshot()) {
					if (syncMsgExecutor == null) {
						log.info("Creating executor for sync message threads");
						syncMsgExecutor = Executors.newFixedThreadPool(threadCount);
					}
					
					syncThreadFutures.add(CompletableFuture.runAsync(() -> {
						final String originalThreadName = Thread.currentThread().getName();
						try {
							setThreadName(msg);
							processMessage(msg);
						}
						finally {
							Thread.currentThread().setName(originalThreadName);
						}
					}, syncMsgExecutor));
				} else {
					final String originalThreadName = Thread.currentThread().getName();
					try {
						setThreadName(msg);
						if (syncThreadFutures.size() > 0) {
							waitForFutures(syncThreadFutures);
							syncThreadFutures.clear();
						}
						
						processMessage(msg);
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
		finally {
			if (syncMsgExecutor != null) {
				log.info("Shutting down executor for sync message threads");
				syncMsgExecutor.shutdown();
				
				try {
					int wait = WAIT_IN_SECONDS + 10;
					log.info("Waiting for " + wait + " seconds for sync threads to terminate");
					
					syncMsgExecutor.awaitTermination(wait, TimeUnit.SECONDS);
					
					log.info("The sync threads have successfully terminated, done shutting down the "
					        + "executor for sync threads");
				}
				catch (Exception e) {
					log.error("An error occurred while waiting for sync threads to terminate");
				}
			}
		}
	}
	
	/**
	 * Processes the specified sync message
	 *
	 * @param msg the sync message to process
	 */
	public void processMessage(SyncMessage msg) {
		
		log.info("Submitting sync message to the processor");
		
		producerTemplate.sendBody(ROUTE_URI_SYNC_PROCESSOR, msg);
		
		if (log.isDebugEnabled()) {
			log.debug("Removing sync message from the queue " + msg);
		}
		
		producerTemplate.sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + msg.getId(), null);
	}
	
	/**
	 * Wait for all the Future instances in the specified list to terminate
	 * 
	 * @param futures the list of Futures instance to wait for
	 * @throws Exception
	 */
	public void waitForFutures(List<CompletableFuture<Void>> futures) throws Exception {
		if (log.isDebugEnabled()) {
			log.debug("Waiting for " + futures.size() + " sync message processor thread(s) to terminate");
		}
		
		CompletableFuture<Void> allFuture = CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()]));
		
		allFuture.get();
		
		if (log.isDebugEnabled()) {
			log.debug(futures.size() + " sync message processor thread(s) have terminated");
		}
	}
	
	private void setThreadName(SyncMessage msg) {
		Thread.currentThread().setName(Thread.currentThread().getName() + ":" + getThreadName(msg));
	}
	
	protected String getThreadName(SyncMessage msg) {
		return msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName()) + "-"
		        + msg.getIdentifier() + "-" + msg.getId();
	}
	
}
