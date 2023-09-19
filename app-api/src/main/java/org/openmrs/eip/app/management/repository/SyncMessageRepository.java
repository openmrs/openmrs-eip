package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncMessageRepository extends JpaRepository<SyncMessage, Long> {
	
	/**
	 * Gets the count of sync messages matching the specified identifier and model class names
	 * 
	 * @param identifier the identifier to match
	 * @param modelClassNames model class names to match
	 * @return count of matching sync messages
	 */
	long countByIdentifierAndModelClassNameIn(String identifier, List<String> modelClassNames);
	
}
