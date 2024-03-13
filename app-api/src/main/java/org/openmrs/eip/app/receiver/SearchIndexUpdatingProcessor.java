package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Updates the OpenMRS search index for entities associated to synced messages.
 */
@Component("searchIndexUpdatingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class SearchIndexUpdatingProcessor extends BasePostSyncActionProcessor {
	
	public SearchIndexUpdatingProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    CustomHttpClient client) {
		super(executor, client, INDEX_RESOURCE);
	}
	
	@Override
	public String getProcessorName() {
		return "search index update";
	}
	
	@Override
	public String getQueueName() {
		return "search-index-update";
	}
	
	@Override
	public void onSuccess(SyncedMessage item) {
		ReceiverUtils.updateColumn("receiver_synced_msg", "search_index_updated", item.getId(), true);
	}
	
	@Override
	public Object convertBody(SyncedMessage item) {
		return ReceiverUtils.generateSearchIndexUpdatePayload(item.getModelClassName(), item.getIdentifier(),
		    item.getOperation());
	}
	
	@Override
	public boolean isSquashed(SyncedMessage item) {
		return item.isSearchIndexUpdated();
	}
	
	@Override
	public void updateSquashedMessage(SyncedMessage item) {
		item.setSearchIndexUpdated(true);
	}
	
}
