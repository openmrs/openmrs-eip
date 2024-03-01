package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.SenderReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SenderReconcileRepository extends JpaRepository<SenderReconciliation, Long> {
	
	/**
	 * Gets the reconciliation.
	 * 
	 * @return the reconciliation
	 */
	@Query(value = "SELECT * FROM sender_reconcile WHERE status <> 'COMPLETED' LIMIT 1", nativeQuery = true)
	SenderReconciliation getReconciliation();
	
}
