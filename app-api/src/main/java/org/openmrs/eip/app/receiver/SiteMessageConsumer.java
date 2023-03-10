package org.openmrs.eip.app.receiver;

import static java.util.Collections.singletonMap;
import static java.util.Collections.synchronizedList;
import static org.openmrs.eip.app.SyncConstants.THREAD_THRESHOLD_MULTIPLIER;
import static org.openmrs.eip.app.receiver.ReceiverConstants.DEFAULT_TASK_BATCH_SIZE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_CONFLICT_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MOVED_TO_ERROR_QUEUE;
import static org.openmrs.eip.app.receiver.ReceiverConstants.EX_PROP_MSG_PROCESSED;
import static org.openmrs.eip.app.receiver.ReceiverConstants.PROP_SYNC_TASK_BATCH_SIZE;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.component.jpa.JpaConstants;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * An instance of this class consumes sync messages for a single site and forwards them to the
 * message processor route
 */
public class SiteMessageConsumer implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SiteMessageConsumer.class);
	
	private static final String PARAM_SITE = "site";
	
	protected static final String ENTITY = SyncMessage.class.getSimpleName();
	
	protected static int batchSize;
	
	private static final String GET_JPA_URI = "jpa:" + ENTITY + "?query=SELECT m FROM " + ENTITY + " m WHERE m.site = :"
	        + PARAM_SITE + " ORDER BY m.dateCreated ASC &maximumResults=" + batchSize;
	
	private static boolean initialized = false;
	
	private static int taskThreshold;
	
	private SiteInfo site;
	
	private boolean errorEncountered = false;
	
	private ProducerTemplate producerTemplate;
	
	private ThreadPoolExecutor executor;
	
	private String messageProcessorUri;
	
	private SyncedMessageRepository syncedMsgRepo;
	
	/**
	 * @param messageProcessorUri the camel endpoint URI to call to process a sync message
	 * @param site sync messages from this site will be consumed by this instance
	 * @param executor {@link ExecutorService} instance to messages in parallel
	 */
	public SiteMessageConsumer(String messageProcessorUri, SiteInfo site, ThreadPoolExecutor executor) {
		this.messageProcessorUri = messageProcessorUri;
		this.site = site;
		this.executor = executor;
		producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		initIfNecessary();
	}
	
	protected void initIfNecessary() {
		synchronized (SiteMessageConsumer.class) {
			if (!initialized) {
				Environment e = SyncContext.getBean(Environment.class);
				batchSize = e.getProperty(PROP_SYNC_TASK_BATCH_SIZE, Integer.class, DEFAULT_TASK_BATCH_SIZE);
				//This ensures there will only be a limited number of queued items for each thread
				taskThreshold = executor.getMaximumPoolSize() * THREAD_THRESHOLD_MULTIPLIER;
				initialized = true;
			}
		}
	}
	
	@Override
	public void run() {
		if (AppUtils.isStopping()) {
			if (log.isDebugEnabled()) {
				log.debug("Sync message consumer skipping execution because the application is stopping");
			}
			
			return;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Starting message consumer thread for site -> " + site);
		}
		
		do {
			Thread.currentThread().setName(site.getIdentifier());
			
			if (log.isTraceEnabled()) {
				log.trace("Fetching next batch of messages to sync for site: " + site);
			}
			
			try {
				List<SyncMessage> syncMessages = fetchNextSyncMessageBatch();
				
				if (syncMessages.isEmpty()) {
					if (log.isTraceEnabled()) {
						log.trace("No sync messages found from site: " + site);
					}
					
					break;
				}
				
				processMessages(syncMessages);
				
			}
			catch (Throwable t) {
				if (!AppUtils.isAppContextStopping()) {
					errorEncountered = true;
					log.error("Message consumer thread for site: " + site + " encountered an error", t);
					break;
				}
			}
			
		} while (!AppUtils.isStopping() && !errorEncountered);
		
		if (!errorEncountered) {
			if (log.isDebugEnabled()) {
				log.debug("Sync message consumer for site: " + site + " has completed");
			}
		}
		
	}
	
	protected List<SyncMessage> fetchNextSyncMessageBatch() throws Exception {
		return producerTemplate.requestBodyAndHeader(GET_JPA_URI, null, JpaConstants.JPA_PARAMETERS_HEADER,
		    singletonMap(PARAM_SITE, site), List.class);
	}
	
	protected void processMessages(List<SyncMessage> syncMessages) throws Exception {
		log.info("Processing " + syncMessages.size() + " message(s) from site: " + site);
		
		List<String> typeAndIdentifier = synchronizedList(new ArrayList(taskThreshold));
		List<CompletableFuture<Void>> futures = synchronizedList(new ArrayList(taskThreshold));
		
		for (SyncMessage msg : syncMessages) {
			if (AppUtils.isAppContextStopping()) {
				log.info("Sync message consumer for site: " + site + " has detected a stop signal");
				break;
			}
			
			//Only process events if they don't belong to the same entity to avoid false conflicts and unique key
			//constraint violations, this applies to subclasses
			if (typeAndIdentifier.contains(msg.getModelClassName() + "#" + msg.getIdentifier())) {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(msg);
					//TODO Record as skipped and go to next
					if (log.isDebugEnabled()) {
						log.debug("Postponed sync of {} because of an earlier unprocessed sync message for the entity", msg);
					}
				}
				finally {
					Thread.currentThread().setName(originalThreadName);
				}
				
				continue;
			}
			
			for (String modelClass : Utils.getListOfModelClassHierarchy(msg.getModelClassName())) {
				typeAndIdentifier.add(modelClass + "#" + msg.getIdentifier());
			}
			
			futures.add(CompletableFuture.runAsync(() -> {
				final String originalThreadName = Thread.currentThread().getName();
				try {
					setThreadName(msg);
					processMessage(msg);
				}
				finally {
					//Maybe we should also remove the entity from typeAndIdentifier list, may be not because there can  
					//be 2 snapshot events for the same entity i.e. for tables with a hierarchy
					Thread.currentThread().setName(originalThreadName);
				}
			}, executor));
			
			if (futures.size() >= taskThreshold) {
				waitForFutures(futures);
				futures.clear();
			}
			
		}
		
		if (futures.size() > 0) {
			waitForFutures(futures);
		}
	}
	
	/**
	 * Processes the specified sync message
	 *
	 * @param msg the sync message to process
	 */
	public void processMessage(SyncMessage msg) {
		if (log.isDebugEnabled()) {
			log.debug("Submitting sync message to the processor");
		}
		
		Exchange exchange = ExchangeBuilder.anExchange(producerTemplate.getCamelContext()).withBody(msg).build();
		
		CamelUtils.send(messageProcessorUri, exchange);
		
		boolean movedToConflict = exchange.getProperty(EX_PROP_MOVED_TO_CONFLICT_QUEUE, false, Boolean.class);
		boolean movedToError = exchange.getProperty(EX_PROP_MOVED_TO_ERROR_QUEUE, false, Boolean.class);
		boolean msgProcessed = exchange.getProperty(EX_PROP_MSG_PROCESSED, false, Boolean.class);
		
		final Long id = msg.getId();
		if (msgProcessed || movedToConflict || movedToError) {
			if (msgProcessed) {
				log.info("Moving the message to the synced queue");
				SyncedMessage syncedMsg = new SyncedMessage(msg);
				syncedMsg.setDateCreated(new Date());
				if (log.isDebugEnabled()) {
					log.debug("Saving synced message");
				}
				
				syncedMsgRepo.save(syncedMsg);
				
				if (log.isDebugEnabled()) {
					log.debug("Successfully saved synced message");
				}
			}
			
			if (log.isDebugEnabled()) {
				log.debug("Removing from the sync message queue an item with id: " + id);
			}
			
			producerTemplate.sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + id, null);
			
			if (log.isDebugEnabled()) {
				log.debug("Successfully removed from sync message queue an item with id: " + id);
			}
		} else {
			throw new EIPException("Something went wrong while processing sync message with id: " + id);
		}
		
		log.info("Done processing message");
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
		return msg.getSite().getIdentifier() + "-" + msg.getMessageUuid() + "-"
		        + AppUtils.getSimpleName(msg.getModelClassName()) + "-" + msg.getIdentifier() + "-" + msg.getId();
	}
	
}
