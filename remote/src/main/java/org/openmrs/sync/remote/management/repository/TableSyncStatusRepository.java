package org.openmrs.sync.remote.management.repository;

import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableSyncStatusRepository extends JpaRepository<TableSyncStatus, Long> {

    long countByTableName(TableNameEnum tableName);
}
