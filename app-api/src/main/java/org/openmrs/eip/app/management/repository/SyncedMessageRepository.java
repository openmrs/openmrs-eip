package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncedMessageRepository extends JpaRepository<SyncedMessage, Long> {
	
	List<SyncedMessage> findFirst1000BySiteAndItemizedOrderByDateCreatedAscIdAsc(SiteInfo site, boolean itemized);
	
}
