package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Gaac;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GaacRepository extends SyncEntityRepository<Gaac> {

    @Override
    @Query("select g from Gaac g " +
            "where g.dateChanged is null and g.dateCreated >= :lastSyncDate " +
            "or g.dateChanged >= :lastSyncDate")
    List<Gaac> findModelsChangedAfterDate(@Param(value = "lastSyncDate") LocalDateTime lastSyncDate);
}
