package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.GaacFamily;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GaacFamilyRepository extends SyncEntityRepository<GaacFamily> {

    @Override
    @Query("select g from GaacFamily g " +
            "where g.dateChanged is null and g.dateCreated >= :lastSyncDate " +
            "or g.dateChanged >= :lastSyncDate")
    List<GaacFamily> findModelsChangedAfterDate(@Param(value = "lastSyncDate") LocalDateTime lastSyncDate);
}