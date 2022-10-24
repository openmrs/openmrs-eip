package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Concept;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConceptRepository extends SyncEntityRepository<Concept> {
	
	@Override
	@Query("select c from Concept c " + "where c.dateChanged is null and c.dateCreated >= :lastSyncDate "
	        + "or c.dateChanged >= :lastSyncDate")
	List<Concept> findModelsChangedAfterDate(@Param("lastSyncDate") final LocalDateTime lastSyncDate);
}
