package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.Encounter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EncounterRepository extends SyncEntityRepository<Encounter> {

    @Override
    @Query("select e from Encounter e " +
            "where e.dateChanged is null and e.dateCreated >= :lastSyncDate " +
            "or e.dateChanged >= :lastSyncDate")
    List<Encounter> findModelsChangedAfterDate(@Param(value = "lastSyncDate") LocalDateTime lastSyncDate);
}
