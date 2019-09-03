package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.PersonName;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonNameRepository extends SyncEntityRepository<PersonName> {

    @Override
    @Query("select p from PersonName p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<PersonName> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
