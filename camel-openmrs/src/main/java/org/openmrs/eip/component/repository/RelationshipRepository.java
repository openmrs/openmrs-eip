package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Relationship;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RelationshipRepository extends SyncEntityRepository<Relationship> {

    @Override
    @Query("select r from Relationship r " +
            "where (r.dateChanged is null and r.dateCreated >= :lastSyncDate) or r.dateChanged >= :lastSyncDate")
    List<Relationship> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);

    @Override
    @Cacheable(cacheNames = "relationshipAll")
    List<Relationship> findAll();
}
