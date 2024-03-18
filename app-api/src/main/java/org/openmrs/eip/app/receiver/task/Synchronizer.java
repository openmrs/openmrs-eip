package org.openmrs.eip.app.receiver.task;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.receiver.BaseQueueSiteTask;
import org.openmrs.eip.app.receiver.processor.SyncMessageProcessor;
import org.openmrs.eip.component.SyncContext;
import org.springframework.data.domain.Pageable;

/**
 * Reads a batch of messages in the sync queue and forwards them to the
 * {@link SyncMessageProcessor}.
 */
public class Synchronizer extends BaseQueueSiteTask<SyncMessage, SyncMessageProcessor> {
	
	public Synchronizer(SiteInfo site) {
		super(site, SyncContext.getBean(SyncMessageProcessor.class));
	}
	
	@Override
	public String getTaskName() {
		return "sync task";
	}
	
	@Override
	public List<SyncMessage> getNextBatch(Pageable page) {
		return syncRepo.getSyncMessageBySiteOrderByDateCreated(site, page);
	}
	
}
