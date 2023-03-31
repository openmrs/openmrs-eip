package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.service.ReceiverArchiveService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a receiver sync archive by moving it to the pruned queue.
 */
@Component("receiverArchivePruningProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverArchivePruningProcessor extends BasePureParallelQueueProcessor<ReceiverSyncArchive> {
	
	protected static final Logger log = LoggerFactory.getLogger(ReceiverArchivePruningProcessor.class);
	
	private ReceiverArchiveService service;
	
	public ReceiverArchivePruningProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor) {
		super(executor);
	}
	
	@Override
	public String getProcessorName() {
		return "archive pruner";
	}
	
	@Override
	public String getQueueName() {
		return "archive-pruner";
	}
	
	@Override
	public String getThreadName(ReceiverSyncArchive item) {
		return item.getMessageUuid();
	}
	
	@Override
	public void processItem(ReceiverSyncArchive item) {
		service.prune(item);
	}
	
}
