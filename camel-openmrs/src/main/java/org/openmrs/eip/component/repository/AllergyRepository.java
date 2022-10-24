package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Allergy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AllergyRepository extends SyncEntityRepository<Allergy> {
	
	@Override
	@Query("select a from Allergy a " + "where a.dateChanged is null and a.dateCreated >= :lastSyncDate "
	        + "or a.dateChanged >= :lastSyncDate")
	List<Allergy> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
