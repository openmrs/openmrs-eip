package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.Concept;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ConceptRepository extends SyncEntityRepository<Concept> {

    @Override
    @Query("select c from Concept c " +
            "where c.dateChanged is null and c.dateCreated >= :lastSyncDate " +
            "or c.dateChanged >= :lastSyncDate")
    List<Concept> findModelsChangedAfterDate(final LocalDateTime lastSyncDate);
}
