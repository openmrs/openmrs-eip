package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Observation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObservationRepository extends SyncEntityRepository<Observation> {
	
	@Override
	@Query("select o from Observation o " + "where o.dateCreated >= :lastSyncDate")
	List<Observation> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
	
}
