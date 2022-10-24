package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Visit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends SyncEntityRepository<Visit> {
	
	@Override
	@Query("select v from Visit v " + "where v.dateChanged is null and v.dateCreated >= :lastSyncDate "
	        + "or v.dateChanged >= :lastSyncDate")
	List<Visit> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
