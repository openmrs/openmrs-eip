package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.PatientIdentifier;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientIdentifierRepository extends SyncEntityRepository<PatientIdentifier> {

    @Override
    @Query("select p from PatientIdentifier p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<PatientIdentifier> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
