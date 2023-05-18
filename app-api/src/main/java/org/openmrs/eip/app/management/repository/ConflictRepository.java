package org.openmrs.eip.app.management.repository;

import java.util.List;

import org.openmrs.eip.app.management.entity.ConflictQueueItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConflictRepository extends JpaRepository<ConflictQueueItem, Long> {
	
	/**
	 * Gets all unresolved conflicts
	 * 
	 * @return list of conflicts
	 */
	List<ConflictQueueItem> findByResolvedIsFalse();
	
}
