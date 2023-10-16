package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Auto resolves conflict items based on the criteria below,
 * 
 * <pre>
 *     1. If the entity has any item in the sync or error queue, auto resolve the conflict with incoming state as the
 *     winner, the logic behind this is that the incoming state for the event in the sync or error queue will anyways
 *     overwrite the receiver state, making the receiver state to be less significant.
 * </pre>
 */
@Component("conflictResolvingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ConflictResolvingProcessor extends BasePureParallelQueueProcessor<ConflictQueueItem> {
	
	protected static final Logger log = LoggerFactory.getLogger(ConflictResolvingProcessor.class);
	
	private ReceiverService receiverService;
	
	private ConflictService conflictService;
	
	public ConflictResolvingProcessor(ReceiverService receiverService, ConflictService conflictService,
	    @Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor) {
		//TODO May be use a separate executor
		super(executor);
		this.receiverService = receiverService;
		this.conflictService = conflictService;
	}
	
	@Override
	public String getProcessorName() {
		return "conflict resolver";
	}
	
	@Override
	public String getQueueName() {
		return "conflict-resolver";
	}
	
	@Override
	public String getThreadName(ConflictQueueItem item) {
		return item.getSite().getIdentifier() + "-" + AppUtils.getSimpleName(item.getModelClassName()) + "-"
		        + item.getIdentifier() + "-" + item.getMessageUuid();
	}
	
	@Override
	public void processItem(ConflictQueueItem item) {
		if (receiverService.hasRetryItem(item.getIdentifier(), item.getModelClassName())
		        || receiverService.hasSyncItem(item.getIdentifier(), item.getModelClassName())) {
			
			ConflictResolution resolution = new ConflictResolution(item, ResolutionDecision.SYNC_NEW);
			try {
				conflictService.resolve(resolution);
			}
			catch (Exception e) {
				//TODO Don't let this item make the task run indefinitely
				log.warn("Failed to resolve conflict with id: " + item);
			}
		}
	}
	
}
