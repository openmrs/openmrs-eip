package org.openmrs.sync.remote.management.init;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.service.TableToSyncEnum;
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
    protected abstract List<TableToSyncEnum> getTablesToSync();

    /**
     * Inits management database with configured table names to sync
     */
    public void start() {
        List<TableSyncStatus> createdStatuses = getTablesToSync().stream()
                .filter(tableToSyncEnum -> repository.countByTableToSync(tableToSyncEnum) == 0)
                .map(this::createEntitySyncStatus)
                .map(status -> repository.save(status))
                .collect(Collectors.toList());

        String createdRows = createdStatuses.stream()
                .map(TableSyncStatus::getTableToSync)
                .map(Enum::name)
                .collect(Collectors.joining(","));

        log.info("Status created for tables: " + createdRows);
    }

    private TableSyncStatus createEntitySyncStatus(final TableToSyncEnum tableToSync) {
        TableSyncStatus tableSyncStatus = new TableSyncStatus();
        tableSyncStatus.setTableToSync(tableToSync);
        return tableSyncStatus;
    }
}
