package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of messages in the synced queue that require updating the OpenMRS search index and
 * forwards them to the {@link SearchIndexUpdatingProcessor}.
 */
public class SearchIndexUpdater extends BasePostSyncActionRunnable {
	
	private SearchIndexUpdatingProcessor processor;
	
	public SearchIndexUpdater(SiteInfo site) {
		super(site);
		processor = SyncContext.getBean(SearchIndexUpdatingProcessor.class);
	}
	
	@Override
	public String getTaskName() {
		return "search index updater task";
	}
	
	@Override
	public void process(List<SyncedMessage> messages) throws Exception {
		processor.processWork(messages);
	}
	
	@Override
	public List<SyncedMessage> getNextBatch() {
		return repo.getBatchOfMessagesForIndexing(site, page);
	}
	
}
