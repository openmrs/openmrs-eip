package org.openmrs.eip.app.management.repository;

import org.openmrs.eip.app.management.entity.TableSyncStatus;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableSyncStatusRepository extends JpaRepository<TableSyncStatus, Long> {

    long countByTableToSync(TableToSyncEnum tableToSync);
}
