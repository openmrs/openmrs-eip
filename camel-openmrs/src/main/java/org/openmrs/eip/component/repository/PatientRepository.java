package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PatientRepository extends SyncEntityRepository<Patient> {
	
	@Override
	@Query("select p from Patient p " + "where p.dateChanged is null and p.dateCreated >= :lastSyncDate "
	        + "or p.dateChanged >= :lastSyncDate")
	List<Patient> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
	
}
