package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of post sync items of type PostSyncActionType.SEARCH_INDEX_UPDATE in the queue that
 * are not yet successfully processed and updates the associated entities in OpenMRS search index.
 */
public class SearchIndexUpdater extends BasePostSyncActionRunnable {
	
	private SearchIndexUpdatingProcessor processor;
	
	public SearchIndexUpdater(SiteInfo site) {
		super(site, PostSyncActionType.SEARCH_INDEX_UPDATE, 100);
		processor = SyncContext.getBean(SearchIndexUpdatingProcessor.class);
	}
	
	@Override
	public String getProcessorName() {
		return "search index updater";
	}
	
	@Override
	public void process(List<PostSyncAction> actions) throws Exception {
		processor.processWork(actions);
	}
	
}
