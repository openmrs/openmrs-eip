package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.entity.receiver.TableReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableReconciliationRepository extends JpaRepository<TableReconciliation, Long> {
	
	/**
	 * Gets a TableReconciliation by SiteReconciliation and table name
	 * 
	 * @param siteReconciliation the SiteReconciliation to match
	 * @param tableName the table name to match
	 * @return TableReconciliation
	 */
	TableReconciliation getBySiteReconciliationAndTableName(SiteReconciliation siteReconciliation, String tableName);
	
}
