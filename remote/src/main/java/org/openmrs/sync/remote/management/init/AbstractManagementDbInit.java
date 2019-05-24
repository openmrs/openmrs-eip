package org.openmrs.sync.remote.management.init;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractManagementDbInit {

    private TableSyncStatusRepository repository;

    public AbstractManagementDbInit(final TableSyncStatusRepository repository) {
        this.repository = repository;
    }

    /**
     * Get the tables to synchronize
     * @return list of enums
     */
    protected abstract List<TableNameEnum> getTablesToSync();

    /**
     * Inits management database with configured table names to sync
     */
    public void start() {
        List<TableSyncStatus> createdStatuses = getTablesToSync().stream()
                .filter(entityNameEnum -> repository.countByTableName(entityNameEnum) == 0)
                .map(this::createTableSyncStatus)
                .map(status -> repository.save(status))
                .collect(Collectors.toList());

        String createdRows = createdStatuses.stream()
                .map(TableSyncStatus::getTableName)
                .map(Enum::name)
                .collect(Collectors.joining(","));

        log.info("Status created for tables: " + createdRows);
    }

    private TableSyncStatus createTableSyncStatus(final TableNameEnum tableNameEnum) {
        TableSyncStatus tableSyncStatus = new TableSyncStatus();
        tableSyncStatus.setTableName(tableNameEnum);
        return tableSyncStatus;
    }
}
