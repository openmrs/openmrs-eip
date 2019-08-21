package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientRepository extends SyncEntityRepository<Patient> {

    @Override
    @Query("select p from Patient p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<Patient> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
