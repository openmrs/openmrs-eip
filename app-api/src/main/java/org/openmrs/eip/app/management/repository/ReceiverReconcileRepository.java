package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReceiverReconcileRepository extends JpaRepository<ReceiverReconciliation, Long> {
	
	/**
	 * Gets the first incomplete reconciliation.
	 * 
	 * @return the reconciliation
	 */
	@Query(value = "SELECT * FROM mgt_reconciliation WHERE status <> 'COMPLETED' LIMIT 1", nativeQuery = true)
	ReceiverReconciliation getReconciliation();
	
}
