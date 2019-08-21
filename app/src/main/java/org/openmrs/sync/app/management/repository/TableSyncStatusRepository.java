package org.openmrs.sync.app.management.repository;

import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.app.management.entity.TableSyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableSyncStatusRepository extends JpaRepository<TableSyncStatus, Long> {

    long countByTableToSync(TableToSyncEnum tableToSync);
}
