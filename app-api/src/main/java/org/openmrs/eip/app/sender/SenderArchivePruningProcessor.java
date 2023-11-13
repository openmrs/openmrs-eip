package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.service.SenderService;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Moves a sync archive to the pruned table.
 */
@Component("senderArchivePruningProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderArchivePruningProcessor extends BasePureParallelQueueProcessor<SenderSyncArchive> {
	
	protected static final Logger log = LoggerFactory.getLogger(SenderArchivePruningProcessor.class);
	
	private SenderService service;
	
	public SenderArchivePruningProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SenderService service) {
		super(executor);
		this.service = service;
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
	public String getThreadName(SenderSyncArchive item) {
		return item.getMessageUuid();
	}
	
	@Override
	public void processItem(SenderSyncArchive item) {
		service.prune(item);
	}
	
}
