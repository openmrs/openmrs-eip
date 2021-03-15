package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.EncounterProvider;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EncounterProviderRepository extends SyncEntityRepository<EncounterProvider> {

    @Override
    @Query("select e from EncounterProvider e " +
            "where e.dateChanged is null and e.dateCreated >= :lastSyncDate " +
            "or e.dateChanged >= :lastSyncDate")
    List<EncounterProvider> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
