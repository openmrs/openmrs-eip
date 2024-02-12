package org.openmrs.eip.app.receiver.reconcile;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.repository.ReconciliationMsgRepository;
import org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask;
import org.openmrs.eip.component.SyncContext;
import org.springframework.data.domain.Pageable;

/**
 * Reads a batch of reconciliation messages and forwards them to the
 * {@link ReconcileMessageProcessor}.
 */
public class ReceiverReconcileMsgTask extends BaseReceiverSyncPrioritizingTask<ReconciliationMessage, ReconcileMessageProcessor> {
	
	private ReconciliationMsgRepository repo;
	
	public ReceiverReconcileMsgTask() {
		super(SyncContext.getBean(ReconcileMessageProcessor.class));
		this.repo = SyncContext.getBean(ReconciliationMsgRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "reconcile msg task";
	}
	
	@Override
	public List<ReconciliationMessage> getNextBatch() {
		return repo.findAll(Pageable.ofSize(Runtime.getRuntime().availableProcessors())).getContent();
	}
	
}
