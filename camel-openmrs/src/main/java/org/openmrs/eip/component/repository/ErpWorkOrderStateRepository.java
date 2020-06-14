package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.ErpWorkOrderState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ErpWorkOrderStateRepository extends SyncEntityRepository<ErpWorkOrderState> {

    @Override
    @Query("SELECT w FROM ErpWorkOrderState w WHERE w.dateCreated >= :lastSyncDate AND w.voided = 0 ORDER BY w.dateCreated ASC")
    List<ErpWorkOrderState> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);

}
