package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteReconciliationRepository extends JpaRepository<SiteReconciliation, Long> {
	
	/**
	 * Gets the site reconciliation instance for the specified site
	 * 
	 * @param site the site to match
	 * @return SiteReconciliation
	 */
	SiteReconciliation getBySite(SiteInfo site);
	
	/**
	 * Gets the count of all completed site reconciliations.
	 * 
	 * @return the count
	 */
	long countByDateCompletedNotNull();
	
}
