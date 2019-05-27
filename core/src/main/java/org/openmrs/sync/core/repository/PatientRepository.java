package org.openmrs.sync.core.repository;

import org.openmrs.sync.core.entity.Patient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PatientRepository extends AuditableRepository<Patient> {

    @Override
    Patient findByUuid(final String uuid);

    @Override
    @Query("select p from Patient p where p.dateChanged is null and p.dateCreated >= :lastSyncDate or p.dateChanged >= :lastSyncDate")
    List<Patient> findModelsChangedAfterDate(@Param("lastSyncDate") LocalDateTime lastSyncDate);
}
