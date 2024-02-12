package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.Reconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReconciliationRepository extends JpaRepository<Reconciliation, Long> {
	
	/**
	 * Gets the first incomplete reconciliation.
	 * 
	 * @return the reconciliation
	 */
	@Query(value = "SELECT * FROM mgt_reconciliation WHERE status <> 'COMPLETED' LIMIT 1", nativeQuery = true)
	Reconciliation getReconciliation();
	
}
