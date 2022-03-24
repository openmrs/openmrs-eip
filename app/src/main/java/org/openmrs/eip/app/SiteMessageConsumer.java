package org.openmrs.eip.app;

import static java.util.Collections.singletonMap;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;
import static org.openmrs.eip.app.SyncConstants.WAIT_IN_SECONDS;

import java.util.List;
import java.util.concurrent.ExecutorService;

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
	
	private ExecutorService syncMsgExecutor;
	
	/**
	 * @param site sync messages from this site will be consumed by this instance
	 * @param syncMsgExecutor ExecutorService object
	 */
	public SiteMessageConsumer(SiteInfo site, ExecutorService syncMsgExecutor) {
		this.site = site;
		this.syncMsgExecutor = syncMsgExecutor;
	}
	
	@Override
	public void run() {
		ProducerTemplate producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		
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
	
	protected void processMessages(List<SyncMessage> syncMessages) {
		log.info("Processing " + syncMessages.size() + " message(s) from site: " + site);
		
		for (SyncMessage msg : syncMessages) {
			if (ReceiverContext.isStopSignalReceived()) {
				log.info("Sync message consumer for site: " + site + " has detected a stop signal");
				break;
			}
			
			if (msg.getSnapshot()) {
				syncMsgExecutor.execute(() -> {
					ReceiverUtils.processMessage(msg);
				});
			}
		}
		
	}
	
}
