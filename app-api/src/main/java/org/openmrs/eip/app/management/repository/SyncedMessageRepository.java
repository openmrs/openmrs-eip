package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SyncedMessageRepository extends JpaRepository<SyncedMessage, Long> {
	
	String UNITEMIZED_QUERY = "SELECT m FROM SyncedMessage m WHERE m.site = :site AND m.itemized = false ORDER BY m.dateCreated ASC, id ASC";
	
	/**
	 * Gets a batch of un itemized synced messages for the specified site
	 *
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of synced messages
	 */
	@Query(UNITEMIZED_QUERY)
	List<SyncedMessage> getBatchOfUnItemizedMessages(@Param("site") SiteInfo site, Pageable pageable);
	
}
