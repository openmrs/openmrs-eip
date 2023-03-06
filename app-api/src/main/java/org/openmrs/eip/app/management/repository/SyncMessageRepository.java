package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface SyncMessageRepository extends JpaRepository<SyncMessage, Long> {
	
	/**
	 * Gets the count of sync messages for the specified site
	 * 
	 * @param site the site to match against
	 * @return the count
	 */
	long countBySite(@Param("site") SiteInfo site);
	
}
