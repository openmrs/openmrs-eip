package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Evicts entities associated to synced messages from the OpenMRS cache.
 */
@Component("cacheEvictingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class CacheEvictingProcessor extends BasePostSyncActionProcessor {
	
	public CacheEvictingProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, CustomHttpClient client) {
		super(executor, client, CACHE_RESOURCE);
	}
	
	@Override
	public String getProcessorName() {
		return "cache evict";
	}
	
	@Override
	public String getQueueName() {
		return "cache-evict";
	}
	
	@Override
	public Object convertBody(SyncedMessage item) {
		return ReceiverUtils.generateEvictionPayload(item.getModelClassName(), item.getIdentifier(), item.getOperation());
	}
	
	@Override
	public void onSuccess(SyncedMessage item) {
		ReceiverUtils.updateColumn("receiver_synced_msg", "evicted_from_cache", item.getId(), true);
	}
	
	@Override
	public boolean isSquashed(SyncedMessage item) {
		return item.isEvictedFromCache();
	}
	
	@Override
	public void updateSquashedMessage(SyncedMessage item) {
		item.setEvictedFromCache(true);
	}
	
}
