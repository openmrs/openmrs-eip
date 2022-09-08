package org.openmrs.eip.app.receiver;

import static java.util.Collections.singletonMap;
import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;
import static org.openmrs.eip.app.receiver.ReceiverConstants.URI_MSG_PROCESSOR;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jpa.JpaConstants;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.utils.Utils;
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
	        + PARAM_SITE + " ORDER BY m.dateCreated ASC, m.id ASC &maximumResults=" + MAX_COUNT;
	
	private SiteInfo site;
	
	private boolean errorEncountered = false;
	
	private ProducerTemplate producerTemplate;
	
	private int threadCount;
	
	private ExecutorService msgExecutor;
	
	private int failureCount;
	
	private ReceiverActiveMqMessagePublisher messagePublisher;
	
	/**
	 * @param site sync messages from this site will be consumed by this instance
	 * @param threadCount the number of threads to use to sync messages in parallel
	 * @param msgExecutor {@link ExecutorService} instance to messages in parallel
	 */
	public SiteMessageConsumer(SiteInfo site, int threadCount, ExecutorService msgExecutor) {
		this.site = site;
		this.threadCount = threadCount;
		this.msgExecutor = msgExecutor;
		failureCount = 0;
	}
	
	@Override
	public void run() {
		producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		messagePublisher = SyncContext.getBean(ReceiverActiveMqMessagePublisher.class);
		
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
						log.warn("Sync message consumer for site: " + site + " has been interrupted");
					}
					
					continue;
				}
				
				processMessages(syncMessages);
				
			}
			catch (Throwable t) {
				if (!AppUtils.isAppContextStopping()) {
					log.error("Message consumer thread for site: " + site + " encountered an error", t);
					
					failureCount++;
					if (failureCount < 3) {
						//TODO Make the wait times configurable
						long wait;
						if (failureCount == 1) {
							wait = 300000;
						} else {
							wait = 900000;
						}
						
						log.info("Pausing message consumer thread for site: " + site + " for " + (wait / 60000)
						        + " minutes after an encountered error");
						
						try {
							Thread.sleep(wait);
						}
						catch (InterruptedException e) {
							log.warn("Sync message consumer for site: " + site + " has been interrupted");
						}
					} else {
						log.error("Stopping message consumer thread for site: " + site);
						
						errorEncountered = true;
						break;
					}
				}
			}
			
		} while (!AppUtils.isAppContextStopping() && !errorEncountered);
		
		log.info("Sync message consumer for site: " + site + " has stopped");
		
		if (errorEncountered) {
			log.info("Shutting down after the sync message consumer for " + site + " encountered an error");
			
			AppUtils.shutdown();
		}
		
	}
	
	protected void processMessages(List<SyncMessage> syncMessages) throws Exception {
		log.info("Processing " + syncMessages.size() + " message(s) from site: " + site);
		
		List<String> typeAndIdentifier = synchronizedList(new ArrayList(threadCount));
		List<CompletableFuture<Void>> syncThreadFutures = synchronizedList(new ArrayList(threadCount));
		
		for (SyncMessage msg : syncMessages) {
			if (AppUtils.isAppContextStopping()) {
				log.info("Sync message consumer for site: " + site + " has detected a stop signal");
				break;
			}
			
			//Only process snapshot events in parallel if they don't belong to the same entity to avoid false conflicts 
			//and unique key constraint violations, this applies to subclasses
			if (msg.getSnapshot() && !typeAndIdentifier.contains(msg.getModelClassName() + "#" + msg.getIdentifier())) {
				for (String modelClass : Utils.getListOfModelClassHierarchy(msg.getModelClassName())) {
					typeAndIdentifier.add(modelClass + "#" + msg.getIdentifier());
				}
				
				//TODO Periodically wait and reset futures to save memory
				syncThreadFutures.add(CompletableFuture.runAsync(() -> {
					final String originalThreadName = Thread.currentThread().getName();
					try {
						setThreadName(msg);
						processMessage(msg);
					}
					finally {
						//May be we should also remove the entity from typeAndIdentifier list but typically there is
						//going to be at most 2 snapshot events for the same entity i.e for tables with a hierarchy
						Thread.currentThread().setName(originalThreadName);
					}
				}, msgExecutor));
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
	
	/**
	 * Processes the specified sync message
	 *
	 * @param msg the sync message to process
	 */
	public void processMessage(SyncMessage msg) {
		messagePublisher.sendSyncResponse(msg);
		
		log.info("Submitting sync message to the processor");
		
		producerTemplate.sendBody(URI_MSG_PROCESSOR, msg);
		
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
