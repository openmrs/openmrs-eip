package org.openmrs.eip.app;

import static java.util.Collections.singletonMap;
import static org.openmrs.eip.app.SyncConstants.MAX_COUNT;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jpa.JpaConstants;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An instance of this class processes sync messages for a single site
 */
public class SiteMessageProcessor implements Runnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SiteMessageProcessor.class);
	
	private static final String PARAM_SITE = "site";
	
	//Order by dateCreated may be just in case the DB is migrated and id change
	private static final String GET_JPA_URI = "jpa:" + SyncMessage.class.getSimpleName() + "?query=SELECT m FROM "
	        + SyncMessage.class.getSimpleName() + " m WHERE m.site = :" + PARAM_SITE + " ORDER BY id ASC &maximumResults="
	        + MAX_COUNT;
	
	private SiteInfo site;
	
	private ProducerTemplate producerTemplate;
	
	/**
	 * @param site sync messages from this site will be processed by this instance
	 * @param producerTemplate {@link ProducerTemplate} object
	 */
	public SiteMessageProcessor(SiteInfo site, ProducerTemplate producerTemplate) {
		this.site = site;
		this.producerTemplate = producerTemplate;
	}
	
	@Override
	public void run() {
		
		do {
			if (log.isDebugEnabled()) {
				log.debug("Fetching next batch of messages to sync for site: " + site);
			}
			
			try {
				List<SyncMessage> syncMessages = producerTemplate.requestBodyAndHeader(GET_JPA_URI, null,
				    JpaConstants.JPA_PARAMETERS_HEADER, singletonMap(PARAM_SITE, site), List.class);
				
				if (syncMessages.isEmpty()) {
					log.info("No sync message found from site: " + site);
					//TODO Make the delay configurable
					Thread.sleep(15000);
					continue;
				}
				
				log.info("Processing " + syncMessages.size() + " message(s) from site: " + site);
				
				for (SyncMessage msg : syncMessages) {
					Thread.currentThread().setName(site.getIdentifier() + "-" + msg.getModelClassName() + "-"
					        + msg.getIdentifier() + "-" + msg.getId());
					
					try {
						producerTemplate.sendBody("direct:message-processor", msg);
					}
					catch (Throwable t) {
						//TODO Gracefully stop this, all other threads, and the application
						//TODO Even better, add a retry mechanism for a number of times before giving up
						log.error("An error occurred while processing message: " + msg, t);
					}
				}
			}
			catch (Throwable t) {
				//TODO After a certain failure count may be we should shutdown the application
				log.error("Exception thrown in thread processing messages for site: " + site, t);
			}
			
		} while (true);
		
	}
	
}
