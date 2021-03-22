package org.openmrs.eip.component.repository;

import org.openmrs.eip.component.entity.PersonAttribute;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonAttributeRepository extends SyncEntityRepository<PersonAttribute> {

    @Override
    @Query("select p from PersonAttribute p " +
            "where p.dateChanged is null and p.dateCreated >= :lastSyncDate " +
            "or p.dateChanged >= :lastSyncDate")
    List<PersonAttribute> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
