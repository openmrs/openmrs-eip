package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationRepository extends SyncEntityRepository<Location> {

    @Override
    @Query("select l from Location l " +
            "where l.dateChanged is null and l.dateCreated >= :lastSyncDate " +
            "or l.dateChanged >= :lastSyncDate")
    List<Location> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
