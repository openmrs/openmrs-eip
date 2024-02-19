package org.openmrs.eip.app.receiver.reconcile;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.openmrs.eip.app.receiver.BaseReceiverSyncPrioritizingTask;
import org.openmrs.eip.component.SyncContext;

public class ReceiverReconcileTask extends BaseReceiverSyncPrioritizingTask<ReceiverReconciliation, ReconciliationProcessor> {
	
	private ReceiverReconcileRepository repo;
	
	public ReceiverReconcileTask() {
		super(SyncContext.getBean(ReconciliationProcessor.class));
		this.repo = SyncContext.getBean(ReceiverReconcileRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "reconcile task";
	}
	
	@Override
	public boolean doRun() throws Exception {
		invokeSuper();
		return true;
	}
	
	protected void invokeSuper() throws Exception {
		super.doRun();
	}
	
	@Override
	public List<ReceiverReconciliation> getNextBatch() {
		return repo.getReconciliation();
	}
	
}
