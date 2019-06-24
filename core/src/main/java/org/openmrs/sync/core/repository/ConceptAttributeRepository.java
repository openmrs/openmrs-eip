package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.ConceptAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConceptAttributeRepository extends SyncEntityRepository<ConceptAttribute> {

    @Override
    @Query("select c from ConceptAttribute c " +
            "where c.dateChanged is null and c.dateCreated >= :lastSyncDate " +
            "or c.dateChanged >= :lastSyncDate")
    List<ConceptAttribute> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
