package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.PersonAddress;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonAddressRepository extends SyncEntityRepository<PersonAddress> {

    @Override
    @Query("select p from PersonAddress p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<PersonAddress> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
