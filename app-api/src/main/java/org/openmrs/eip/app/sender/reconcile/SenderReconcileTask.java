package org.openmrs.eip.app.sender.reconcile;

import java.util.Collections;
import java.util.List;

import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.openmrs.eip.app.management.repository.SenderReconcileRepository;
import org.openmrs.eip.component.SyncContext;

/**
 * Task that executes the sender reconciliation.
 */
public class SenderReconcileTask extends BaseDelegatingQueueTask<SenderReconciliation, SenderReconcileProcessor> {
	
	private SenderReconcileRepository repo;
	
	public SenderReconcileTask() {
		super(SyncContext.getBean(SenderReconcileProcessor.class));
		this.repo = SyncContext.getBean(SenderReconcileRepository.class);
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
	public List<SenderReconciliation> getNextBatch() {
		SenderReconciliation rec = repo.getReconciliation();
		return rec == null ? Collections.emptyList() : List.of(rec);
	}
	
}
