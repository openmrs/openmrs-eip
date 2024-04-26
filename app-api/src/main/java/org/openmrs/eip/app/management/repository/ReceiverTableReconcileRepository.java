package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverTableReconciliation;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReceiverTableReconcileRepository extends JpaRepository<ReceiverTableReconciliation, Long> {
	
	/**
	 * Gets a ReceiverTableReconciliation by SiteReconciliation and table name
	 * 
	 * @param siteReconciliation the SiteReconciliation to match
	 * @param tableName the table name to match
	 * @return TableReconciliation
	 */
	ReceiverTableReconciliation getBySiteReconciliationAndTableName(SiteReconciliation siteReconciliation, String tableName);
	
	/**
	 * Gets the count of all completed tables matching the specified site reconciliation.
	 * 
	 * @param siteRec the site reconciliation to match
	 * @return the count of matches
	 */
	long countByCompletedIsTrueAndSiteReconciliation(SiteReconciliation siteRec);
	
	/**
	 * Gets all incomplete table reconciliations matching the specified site reconciliation.
	 *
	 * @param siteRec the site reconciliation to match
	 * @return list of receiver table reconciliations
	 */
	List<ReceiverTableReconciliation> getByCompletedIsFalseAndSiteReconciliation(SiteReconciliation siteRec);
	
	/**
	 * Gets all table reconciliations belonging to the specified site reconciliation.
	 *
	 * @param siteRec the site reconciliation to match
	 * @return list of receiver table reconciliations
	 */
	List<ReceiverTableReconciliation> getBySiteReconciliation(SiteReconciliation siteRec);
	
	/**
	 * Gets all reconciled table names matching the specified site reconciliation.
	 *
	 * @param siteRec the site reconciliation to match
	 * @return list of all reconciled table names
	 */
	@Query("SELECT lower(r.tableName) FROM ReceiverTableReconciliation r WHERE r.completed = true AND r.siteReconciliation = :siteRec")
	List<String> getReconciledTables(SiteReconciliation siteRec);
	
}
