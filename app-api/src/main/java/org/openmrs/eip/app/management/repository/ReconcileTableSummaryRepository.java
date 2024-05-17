package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReconcileTableSummary;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReconcileTableSummaryRepository extends JpaRepository<ReconcileTableSummary, Long> {
	
	String QUERY_TOTALS = "SELECT SUM(s.missingCount), SUM(s.missingSyncCount), SUM(s.missingErrorCount), "
	        + "SUM(s.undeletedCount), SUM(s.undeletedSyncCount), SUM(s.undeletedErrorCount) FROM "
	        + "ReconcileTableSummary s WHERE s.reconciliation = :rec";
	
	String QUERY_SITE_TOTALS = QUERY_TOTALS + " AND s.site = :site";
	
	/**
	 * Get the count totals for the specified reconciliation.
	 * 
	 * @param rec the reconciliation to match
	 * @return list containing an array of totals
	 */
	@Query(QUERY_TOTALS)
	List<Object[]> getCountTotals(ReceiverReconciliation rec);
	
	/**
	 * Get the count totals for the specified reconciliation and site.
	 * 
	 * @param rec the reconciliation to match
	 * @param site the site to match
	 * @return list containing an array of totals
	 */
	@Query(QUERY_SITE_TOTALS)
	List<Object[]> getCountTotalsBySite(ReceiverReconciliation rec, SiteInfo site);
	
}
