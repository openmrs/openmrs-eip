package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.WorkOrderState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkOrderStateRepository extends SyncEntityRepository<WorkOrderState> {

    @Override
    @Query("SELECT w FROM WorkOrderState w WHERE w.dateCreated >= :lastSyncDate AND w.voided = 0 ORDER BY w.dateCreated ASC")
    List<WorkOrderState> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);

}
