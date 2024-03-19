package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConflictRepository extends JpaRepository<ConflictQueueItem, Long> {
	
	/**
	 * Gets the ids of all the existing conflicts
	 * 
	 * @return list of ids
	 */
	@Query("SELECT id FROM ConflictQueueItem ORDER BY dateReceived ASC")
	List<Long> getConflictIds();
	
	/**
	 * Gets the count of conflicts matching the specified identifier and model class names
	 * 
	 * @param identifier the identifier
	 * @param modelClassNames the model class names
	 * @return count of conflicts
	 */
	long countByIdentifierAndModelClassNameIn(String identifier, List<String> modelClassNames);
	
}
