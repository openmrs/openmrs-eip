package org.openmrs.sync.component.repository;

import org.openmrs.sync.component.entity.Allergy;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface AllergyRepository extends SyncEntityRepository<Allergy> {

    @Override
    @Query("select a from Allergy a " +
            "where a.dateChanged is null and a.dateCreated >= :lastSyncDate " +
            "or a.dateChanged >= :lastSyncDate")
    List<Allergy> findModelsChangedAfterDate(LocalDateTime lastSyncDate);
}
