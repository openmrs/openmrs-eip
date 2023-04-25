package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.ReceiverSyncStatus;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteSyncStatusRepository extends JpaRepository<ReceiverSyncStatus, Long> {
	
	/**
	 * Gets the sync status for the specified site
	 * 
	 * @param siteInfo the site
	 * @return ReceiverSyncStatus
	 */
	ReceiverSyncStatus findBySiteInfo(SiteInfo siteInfo);
	
}
