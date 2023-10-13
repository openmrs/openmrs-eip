package org.openmrs.eip.app.receiver;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BaseQueueProcessor;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.service.ConflictService;
import org.openmrs.eip.app.management.service.ReceiverService;
import org.openmrs.eip.app.receiver.ConflictResolution.ResolutionDecision;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 *
 * <pre>
 *     2. The state with the latest applicable date field value wins e.g. date_changed, date_voided, the logic behind
 *     this is that whoever last modified the entity made the decisive change.
 * </pre>
 */
@Component("conflictResolvingProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ConflictResolvingProcessor extends BaseQueueProcessor<ConflictQueueItem> {
	
	protected static final Logger log = LoggerFactory.getLogger(ConflictResolvingProcessor.class);
	
	private ReceiverService receiverService;
	
	private ConflictService conflictService;
	
	private ConflictResolvingProcessor() {
		//TODO May be use a separate executor
		super(SyncContext.getBean(BEAN_NAME_SYNC_EXECUTOR));
		this.receiverService = SyncContext.getBean(ReceiverService.class);
		this.conflictService = SyncContext.getBean(ConflictService.class);
	}
	
	public static ConflictResolvingProcessor getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	@Override
	public String getProcessorName() {
		return "conflict resolver";
	}
	
	@Override
	public String getUniqueId(ConflictQueueItem item) {
		return item.getId().toString();
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
	public String getLogicalType(ConflictQueueItem item) {
		return item.getClass().getName();
	}
	
	@Override
	public List<String> getLogicalTypeHierarchy(String logicalType) {
		return null;
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
	
	private static class InstanceHolder {
		
		private static ConflictResolvingProcessor INSTANCE = new ConflictResolvingProcessor();
		
	}
	
}
