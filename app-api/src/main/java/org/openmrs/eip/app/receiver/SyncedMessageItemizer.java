package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads and processes non itemized messages in the synced queue to determine those that requires
 * eviction from the OpenMRS cache and updating the search index
 */
public class SyncedMessageItemizer extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncedMessageItemizer.class);
	
	private SyncedMessageRepository syncedMsgRepo;
	
	private SyncedMessageItemizingProcessor processor;
	
	public SyncedMessageItemizer(SiteInfo site) {
		super(site);
		syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		processor = SyncContext.getBean(SyncedMessageItemizingProcessor.class);
	}
	
	@Override
	public String getTaskName() {
		return "msg itemizer task";
	}
	
	@Override
	public boolean doRun() throws Exception {
		if (log.isTraceEnabled()) {
			log.trace("Fetching next batch of " + page.getPageSize() + " messages to itemize for site: " + site);
		}
		
		List<SyncedMessage> messages = syncedMsgRepo.getBatchOfMessagesForItemizing(site, page);
		
		if (messages.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No synced messages for itemizing found for site: " + site);
			}
			
			return true;
		}
		
		if (log.isDebugEnabled()) {
			log.debug("Itemizing " + messages.size() + " message(s) for site: " + site);
		}
		
		processor.processWork(messages);
		
		return false;
	}
	
}
