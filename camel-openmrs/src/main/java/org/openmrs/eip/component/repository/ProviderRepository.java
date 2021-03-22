package org.openmrs.eip.component.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.openmrs.eip.component.entity.Provider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProviderRepository extends SyncEntityRepository<Provider> {

    @Override
    @Query(" select p from Provider p " +
           " where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
           " or p.dateChanged >= :lastSyncDate")
    List<Provider> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
    
    @Override
    @Cacheable(cacheNames = "providerAll")
    List<Provider> findAll();
}