package org.openmrs.eip.app.receiver;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes all non itemized messages in the synced queue to generate PostSyncActions
 */
public class SyncedMessageItemizer extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(SyncedMessageItemizer.class);
	
	private ProducerTemplate producerTemplate;
	
	private SyncedMessageRepository syncedMsgRepo;
	
	private SyncedMessageItemizingProcessor processor;
	
	public SyncedMessageItemizer(SiteInfo site) {
		super(site);
		producerTemplate = SyncContext.getBean(ProducerTemplate.class);
		syncedMsgRepo = SyncContext.getBean(SyncedMessageRepository.class);
		processor = SyncContext.getBean(SyncedMessageItemizingProcessor.class);
	}
	
	@Override
	public String getProcessorName() {
		return "Synced message itemizer";
	}
	
	@Override
	public boolean doRun() throws Exception {
		//TODO Check for existence of sync messages before running
		if (log.isTraceEnabled()) {
			log.trace("Fetching next batch of messages to itemize for site: " + getSite());
		}
		
		List<SyncedMessage> messages = syncedMsgRepo.findFirst1000BySiteAndItemizedOrderByDateCreatedAscIdAsc(getSite(),
		    false);
		if (messages.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No synced messages found from site: " + getSite());
			}
			
			return true;
		}
		
		log.info("Itemizing " + messages.size() + " message(s) for site: " + getSite());
		
		Exchange exchange = ExchangeBuilder.anExchange(producerTemplate.getCamelContext()).withBody(messages).build();
		
		processor.process(exchange);
		
		return false;
	}
	
}
