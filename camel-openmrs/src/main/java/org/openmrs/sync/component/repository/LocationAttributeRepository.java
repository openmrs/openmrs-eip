package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.LocationAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationAttributeRepository extends SyncEntityRepository<LocationAttribute> {

    @Override
    @Query("select l from LocationAttribute l " +
            "where l.dateChanged is null and l.dateCreated >= :lastSyncDate " +
            "or l.dateChanged >= :lastSyncDate")
    List<LocationAttribute> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
