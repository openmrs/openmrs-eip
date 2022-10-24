package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.PersonAddress;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonAddressRepository extends SyncEntityRepository<PersonAddress> {
	
	@Override
	@Query("select p from PersonAddress p " + "where p.dateChanged is null and p.dateCreated >= :lastSyncDate "
	        + "or p.dateChanged >= :lastSyncDate")
	List<PersonAddress> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
