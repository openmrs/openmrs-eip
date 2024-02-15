package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.sender.SenderTableReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SenderTableReconcileRepository extends JpaRepository<SenderTableReconciliation, Long> {
	
	/**
	 * Gets the SenderTableReconciliation matching the specified table name.
	 * 
	 * @param table the name of the table to match
	 * @return SenderTableReconciliation object
	 */
	SenderTableReconciliation getByTableName(String table);
	
}
