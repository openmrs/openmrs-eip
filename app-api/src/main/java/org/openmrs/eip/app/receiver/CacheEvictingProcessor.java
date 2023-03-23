package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Evicts entities associated to synced messages from the OpenMRS cache.
 */
@Component("cacheEvictingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class CacheEvictingProcessor extends BaseSendToCamelPostSyncActionProcessor {
	
	protected static final Logger log = LoggerFactory.getLogger(CacheEvictingProcessor.class);
	
	public CacheEvictingProcessor(ProducerTemplate producerTemplate,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, SyncedMessageRepository repo) {
		super(ReceiverConstants.URI_CLEAR_CACHE, producerTemplate, executor, repo);
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
		if (!item.isEvictedFromCache()) {
			item.setEvictedFromCache(true);
		}
		
		repo.save(item);
	}
	
	@Override
	public boolean skipSend(SyncedMessage item) {
		return item.isEvictedFromCache();
	}
	
	@Override
	public void updateSquashedMessage(SyncedMessage item) {
		item.setEvictedFromCache(true);
	}
	
}
