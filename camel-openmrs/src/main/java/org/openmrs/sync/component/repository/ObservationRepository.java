package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.Observation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ObservationRepository extends SyncEntityRepository<Observation> {

    @Override
    @Query("select o from Observation o " +
            "where o.dateCreated >= :lastSyncDate")
    List<Observation> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
