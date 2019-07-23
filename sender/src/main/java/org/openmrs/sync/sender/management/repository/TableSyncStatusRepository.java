package org.openmrs.sync.sender.management.repository;

import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.sender.management.entity.TableSyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableSyncStatusRepository extends JpaRepository<TableSyncStatus, Long> {

    long countByTableToSync(TableToSyncEnum tableToSync);
}
