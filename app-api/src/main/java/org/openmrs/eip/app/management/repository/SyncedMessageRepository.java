package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncedMessageRepository extends JpaRepository<SyncedMessage, Long> {
	
	/**
	 * Fetches the first 1000 synced messages for the specified site
	 * 
	 * @param site the {@link SiteInfo} to match against
	 * @param itemized specifies whether itemized messages should be included or not
	 * @return list of synced messages
	 */
	List<SyncedMessage> findFirst1000BySiteAndItemizedOrderByDateCreatedAscIdAsc(SiteInfo site, boolean itemized);
	
}
