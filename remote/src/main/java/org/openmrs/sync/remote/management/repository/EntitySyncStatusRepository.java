package org.openmrs.sync.remote.management.repository;

import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.entity.EntitySyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntitySyncStatusRepository extends JpaRepository<EntitySyncStatus, Long> {

    long countByEntityName(EntityNameEnum entityName);
}
