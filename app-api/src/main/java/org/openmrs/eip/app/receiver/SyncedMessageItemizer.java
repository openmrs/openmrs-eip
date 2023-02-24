package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Processes all non itemized messages in the synced queue to generate PostSyncActions
 */
public class SyncedMessageItemizer extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncedMessageItemizer.class);
	
	private SyncedMessageRepository syncedMsgRepo;
	
	private SyncedMessageItemizingProcessor processor;
	
	private Pageable page;
	
	public SyncedMessageItemizer(SiteInfo site) {
		super(site);
		syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		processor = SyncContext.getBean(SyncedMessageItemizingProcessor.class);
		//TODO Configure batch size
		page = PageRequest.of(0, 1000);
	}
	
	@Override
	public String getProcessorName() {
		return "Synced message itemizer";
	}
	
	@Override
	public boolean doRun() throws Exception {
		//TODO Check for existence of sync messages before running
		if (log.isTraceEnabled()) {
			log.trace("Fetching next batch of " + page.getPageSize() + " messages to itemize for site: " + getSite());
		}
		
		List<SyncedMessage> messages = syncedMsgRepo.getBatchOfUnItemizedMessages(getSite(), page);
		
		if (messages.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No synced messages found from site: " + getSite());
			}
			
			return true;
		}
		
		log.info("Itemizing " + messages.size() + " message(s) for site: " + getSite());
		
		processor.processWork(messages);
		
		return false;
	}
	
}
