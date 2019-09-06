package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.Condition;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ConditionRepository extends SyncEntityRepository<Condition> {

    @Override
    @Query("select c from Condition c " +
            "where c.dateCreated >= :lastSyncDate")
    List<Condition> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
