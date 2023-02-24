package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.PostSyncAction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostSyncActionRepository extends JpaRepository<PostSyncAction, Long> {
	
	String RESPONSE_QUERY = "SELECT a FROM PostSyncAction a WHERE a.message.site = :site AND a.actionType = "
	        + "'SEND_RESPONSE' AND a.status != 'SUCCESS'";
	
	/**
	 * Gets a batch the unprocessed response post sync actions i.e. new and failed actions
	 * 
	 * @param site the site to match against
	 * @param pageable {@link Pageable} instance
	 * @return list of new or response actions
	 */
	@Query(RESPONSE_QUERY)
	List<PostSyncAction> getBatchOfPendingResponseActions(@Param("site") SiteInfo site, Pageable pageable);
	
}
