package org.openmrs.eip.app.receiver;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction.PostSyncActionType;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of post sync items of type PostSyncActionType.CACHE_EVICT in the queue that are not
 * yet successfully processed and evicts the associated entities from the OpenMRS cache.
 */
public class CacheEvictor extends BasePostSyncActionRunnable {
	
	private CacheEvictingProcessor processor;
	
	public CacheEvictor(SiteInfo site) {
		super(site, PostSyncActionType.CACHE_EVICT, 100);
		processor = SyncContext.getBean(CacheEvictingProcessor.class);
	}
	
	@Override
	public String getProcessorName() {
		return "cache evictor";
	}
	
	@Override
	public void process(List<PostSyncAction> actions) throws Exception {
		processor.processWork(actions);
	}
	
}
