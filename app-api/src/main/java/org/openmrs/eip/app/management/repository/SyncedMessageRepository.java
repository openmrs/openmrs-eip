package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SyncedMessageRepository extends JpaRepository<SyncedMessage, Long> {
	
	String RESPONSE_QUERY = "SELECT m FROM SyncedMessage m WHERE m.site = :site AND m.responseSent = false";
	
	String EVICT_QUERY = "SELECT m FROM SyncedMessage m WHERE m.site = :site AND m.outcome = 'SUCCESS' AND "
	        + "m.cached = true AND m.evictedFromCache = false ORDER BY m.dateCreated ASC";
	
	String INDEX_QUERY = "SELECT m FROM SyncedMessage m WHERE m.site = :site AND m.outcome = 'SUCCESS' AND "
	        + "m.indexed = true AND m.searchIndexUpdated = false AND (m.cached = false OR m.evictedFromCache = true) "
	        + "ORDER BY m.dateCreated ASC";
	
	String ARCHIVE_QUERY = "SELECT m FROM SyncedMessage m WHERE m.site = :site AND m.outcome = 'SUCCESS' AND "
	        + "m.responseSent = true AND (m.cached = false OR m.evictedFromCache = true) AND (m.indexed = false OR "
	        + "m.searchIndexUpdated = true)";
	
	/**
	 * Gets a batch of messages for which responses have not yet been sent
	 *
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of synced messages
	 */
	@Query(RESPONSE_QUERY)
	List<SyncedMessage> getBatchOfMessagesForResponse(@Param("site") SiteInfo site, Pageable pageable);
	
	/**
	 * Gets a batch of messages ordered by ascending date created for cached entities for which
	 * evictions have not yet been done.
	 *
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of synced messages
	 */
	@Query(EVICT_QUERY)
	List<SyncedMessage> getBatchOfMessagesForEviction(@Param("site") SiteInfo site, Pageable pageable);
	
	/**
	 * Gets a batch of messages ordered by ascending date created for indexed entities for which the
	 * index have not yet been updated.
	 *
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of synced messages
	 */
	@Query(INDEX_QUERY)
	List<SyncedMessage> getBatchOfMessagesForIndexing(@Param("site") SiteInfo site, Pageable pageable);
	
	/**
	 * Gets a batch of post processed synced messages for archiving for the specified site
	 *
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of synced messages
	 */
	@Query(ARCHIVE_QUERY)
	List<SyncedMessage> getBatchOfMessagesForArchiving(@Param("site") SiteInfo site, Pageable pageable);
	
}
