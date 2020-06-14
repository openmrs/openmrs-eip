package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.PatientState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientStateRepository extends SyncEntityRepository<PatientState> {

    @Override
    @Query("select p from PatientState p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<PatientState> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
