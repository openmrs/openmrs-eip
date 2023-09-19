package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiverRetryRepository extends JpaRepository<ReceiverRetryQueueItem, Long> {
	
	/**
	 * Gets the count of retry items matching the specified identifier and model class names
	 *
	 * @param identifier the identifier to match
	 * @param modelClassNames model class names to match
	 * @return count of matching retry items
	 */
	long countByIdentifierAndModelClassNameIn(String identifier, List<String> modelClassNames);
	
}
