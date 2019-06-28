package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.Location;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends SyncEntityRepository<Location> {

    @Override
    @Query("select l from Location l " +
            "where l.dateChanged is null and l.dateCreated >= :lastSyncDate " +
            "or l.dateChanged >= :lastSyncDate")
    List<Location> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
