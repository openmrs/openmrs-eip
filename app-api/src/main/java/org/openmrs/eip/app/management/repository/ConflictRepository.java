package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
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
	
}
