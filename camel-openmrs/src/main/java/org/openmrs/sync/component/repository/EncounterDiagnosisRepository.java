package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.EncounterDiagnosis;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface EncounterDiagnosisRepository extends SyncEntityRepository<EncounterDiagnosis> {

    @Override
    @Query("select e from EncounterDiagnosis e " +
            "where e.dateChanged is null and e.dateCreated >= :lastSyncDate " +
            "or e.dateChanged >= :lastSyncDate")
    List<EncounterDiagnosis> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
