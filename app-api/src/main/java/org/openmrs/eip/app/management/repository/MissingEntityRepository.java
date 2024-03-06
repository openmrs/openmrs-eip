package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.receiver.MissingEntity;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MissingEntityRepository extends JpaRepository<MissingEntity, Long> {
	
	/**
	 * Gets the count of missing entities for the specified site and table.
	 *
	 * @param site the site to match
	 * @param table the table to match
	 * @return count of matches
	 */
	long countBySiteAndTableNameIgnoreCase(SiteInfo site, String table);
	
	/**
	 * Gets the count of missing entities for the specified site and table that exist in the sync queue.
	 *
	 * @param site the site to match
	 * @param table the table to match
	 * @return count of matches
	 */
	long countBySiteAndTableNameIgnoreCaseAndInSyncQueueTrue(SiteInfo site, String table);
	
	/**
	 * Gets the count of missing entities for the specified site and table that exist in the error
	 * queue.
	 *
	 * @param site the site to match
	 * @param table the table to match
	 * @return count of matches
	 */
	long countBySiteAndTableNameIgnoreCaseAndInErrorQueueTrue(SiteInfo site, String table);
	
}
