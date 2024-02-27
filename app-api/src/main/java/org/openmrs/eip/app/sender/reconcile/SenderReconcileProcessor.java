package org.openmrs.eip.app.sender.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation.SenderReconcileStatus;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.management.service.SenderReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Processes a SenderReconciliation item
 */
@Component("senderReconcileProcessor")
@Profile(SyncProfiles.SENDER)
public class SenderReconcileProcessor extends BasePureParallelQueueProcessor<SenderReconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(SenderReconcileProcessor.class);
	
	private SenderReconcileRepository reconcileRepo;
	
	private SenderTableReconcileRepository tableReconcileRepo;
	
	private SenderReconcileService service;
	
	public SenderReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SenderReconcileRepository reconcileRepo, SenderTableReconcileRepository tableReconcileRepo,
	    SenderReconcileService service) {
		super(executor);
		this.reconcileRepo = reconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
		this.service = service;
	}
	
	@Override
	public String getProcessorName() {
		return "reconcile";
	}
	
	@Override
	public String getQueueName() {
		return "reconcile";
	}
	
	@Override
	public String getThreadName(SenderReconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(SenderReconciliation reconciliation) {
		switch (reconciliation.getStatus()) {
			case NEW:
				initialize(reconciliation);
				break;
			case PROCESSING:
				update(reconciliation);
				break;
			case POST_PROCESSING:
				finalise(reconciliation);
				break;
			case COMPLETED:
				throw new EIPException("Reconciliation is already completed");
		}
	}
	
	private void initialize(SenderReconciliation reconciliation) {
		List<SenderTableReconciliation> snapshots = service.takeSnapshot();
		service.saveSnapshot(reconciliation, snapshots);
	}
	
	private void update(SenderReconciliation reconciliation) {
		List<String> incompleteTables = AppUtils.getTablesToSync().stream()
		        .filter(t -> !tableReconcileRepo.getByTableNameIgnoreCase(t).isCompleted()).toList();
		if (incompleteTables.isEmpty()) {
			reconciliation.setStatus(SenderReconcileStatus.POST_PROCESSING);
			LOG.info("Updating reconciliation status to " + reconciliation.getStatus());
			if (LOG.isTraceEnabled()) {
				LOG.debug("Saving updated reconciliation");
			}
			
			reconcileRepo.save(reconciliation);
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("There is still {} incomplete table reconciliation(s)", incompleteTables.size());
			}
		}
	}
	
	private void finalise(SenderReconciliation reconciliation) {
		//TODO Check for deletes and mark as completed
	}
	
}
