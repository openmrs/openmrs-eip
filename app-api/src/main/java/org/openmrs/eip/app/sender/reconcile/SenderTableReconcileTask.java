package org.openmrs.eip.app.sender.reconcile;

import java.util.List;

import org.openmrs.eip.app.BaseDelegatingQueueTask;
import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.openmrs.eip.app.management.repository.SenderTableReconcileRepository;
import org.openmrs.eip.app.receiver.reconcile.ReconcileMessageProcessor;
import org.openmrs.eip.component.SyncContext;

/**
 * Reads a batch of table reconciliations and forwards them to the
 * {@link ReconcileMessageProcessor}.
 */
public class SenderTableReconcileTask extends BaseDelegatingQueueTask<SenderTableReconciliation, SenderTableReconcileProcessor> {
	
	private SenderTableReconcileRepository repo;
	
	public SenderTableReconcileTask() {
		super(SyncContext.getBean(SenderTableReconcileProcessor.class));
		this.repo = SyncContext.getBean(SenderTableReconcileRepository.class);
	}
	
	@Override
	public String getTaskName() {
		return "table reconcile task";
	}
	
	@Override
	public List<SenderTableReconciliation> getNextBatch() {
		return repo.getIncompleteReconciliations();
	}
	
}
