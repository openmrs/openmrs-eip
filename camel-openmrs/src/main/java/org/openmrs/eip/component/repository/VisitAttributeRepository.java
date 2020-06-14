package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.VisitAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitAttributeRepository extends SyncEntityRepository<VisitAttribute> {

    @Override
    @Query("select v from VisitAttribute v " +
            "where v.dateChanged is null and v.dateCreated >= :lastSyncDate " +
            "or v.dateChanged >= :lastSyncDate")
    List<VisitAttribute> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
