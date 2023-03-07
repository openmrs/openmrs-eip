package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Superclass for post sync action processor tasks
 */
public abstract class BasePostSyncActionRunnable extends BaseSiteRunnable {
	
	protected static final Logger log = LoggerFactory.getLogger(BasePostSyncActionRunnable.class);
	
	protected SyncedMessageRepository repo;
	
	public BasePostSyncActionRunnable(SiteInfo site) {
		super(site);
		repo = SyncContext.getBean(SyncedMessageRepository.class);
	}
	
	@Override
	public boolean doRun() throws Exception {
		List<SyncedMessage> messages = getNextBatch();
		if (messages.isEmpty()) {
			if (log.isTraceEnabled()) {
				log.trace("No messages found by " + getTaskName() + " for site: " + site);
			}
			
			return true;
		}
		
		process(messages);
		
		return false;
	}
	
	/**
	 * Gets the next batch of messages to process
	 *
	 * @return List of messages
	 */
	public abstract List<SyncedMessage> getNextBatch();
	
	/**
	 * Processes the specified list of messages
	 * 
	 * @param messages list of messages
	 * @throws Exception
	 */
	public abstract void process(List<SyncedMessage> messages) throws Exception;
	
}
