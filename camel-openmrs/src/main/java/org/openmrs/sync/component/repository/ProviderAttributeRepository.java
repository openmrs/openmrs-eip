package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.ProviderAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ProviderAttributeRepository extends SyncEntityRepository<ProviderAttribute> {

    @Override
    @Query("select p from ProviderAttribute p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<ProviderAttribute> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
