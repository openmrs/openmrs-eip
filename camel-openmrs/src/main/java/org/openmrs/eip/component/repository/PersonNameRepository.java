package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.PersonName;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonNameRepository extends SyncEntityRepository<PersonName> {
	
	@Override
	@Query("select p from PersonName p " + "where p.dateChanged is null and p.dateCreated >= :lastSyncDate "
	        + "or p.dateChanged >= :lastSyncDate")
	List<PersonName> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
