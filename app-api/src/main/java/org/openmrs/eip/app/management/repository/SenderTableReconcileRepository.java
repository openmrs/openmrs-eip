package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SenderTableReconcileRepository extends JpaRepository<SenderTableReconciliation, Long> {
	
	/**
	 * Gets the SenderTableReconciliation matching the specified table name.
	 * 
	 * @param table the name of the table to match
	 * @return SenderTableReconciliation object
	 */
	SenderTableReconciliation getByTableNameIgnoreCase(String table);
	
	/**
	 * Gets the SenderTableReconciliation that are not completed
	 *
	 * @return List of SenderTableReconciliation objects
	 */
	@Query("SELECT r FROM SenderTableReconciliation r WHERE r.started = false OR r.lastProcessedId < r.endId")
	List<SenderTableReconciliation> getIncompleteReconciliations();
	
}
