package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation.ReconciliationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReceiverReconcileRepository extends JpaRepository<ReceiverReconciliation, Long> {
	
	/**
	 * Gets the first incomplete reconciliation.
	 * 
	 * @return the reconciliation
	 */
	@Query(value = "SELECT * FROM receiver_reconcile WHERE status <> 'COMPLETED' LIMIT 1", nativeQuery = true)
	ReceiverReconciliation getReconciliation();
	
	/**
	 * Get the 3 most recent completed reconciliations.
	 *
	 * @return a list of reconciliations
	 */
	List<ReceiverReconciliation> getTop3ByStatusOrderByDateCreatedDesc(ReconciliationStatus status);
	
}
