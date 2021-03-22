package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.Person;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonRepository extends SyncEntityRepository<Person> {

    @Override
    @Query("select p from Person p " +
            "where (p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate)")
    List<Person> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);

    @Override
    @Cacheable(cacheNames = "personAll")
    List<Person> findAll();
}
