package org.openmrs.eip.app;

import static org.openmrs.eip.app.SiteMessageConsumer.ENTITY;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains receiver utility methods
 */
public class ReceiverUtils {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverUtils.class);
	
	private static ProducerTemplate producerTemplate;
	
	/**
	 * Processes the specified sync message
	 * 
	 * @param msg the sync message to process
	 */
	public static void processMessage(SyncMessage msg) {
		Thread.currentThread().setName(msg.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(msg.getModelClassName())
		        + "-" + msg.getIdentifier() + "-" + msg.getId());
		
		log.info("Submitting sync message to the processor");
		
		if (producerTemplate == null) {
			producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		}
		
		producerTemplate.sendBody("direct:message-processor", msg);
		
		if (log.isDebugEnabled()) {
			log.debug("Removing sync message from the queue");
		}
		
		producerTemplate.sendBody("jpa:" + ENTITY + "?query=DELETE FROM " + ENTITY + " WHERE id = " + msg.getId(), null);
	}
	
}
