package org.openmrs.sync.remote.management.init;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.entity.EntitySyncStatus;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractManagementDbInit {

    private EntitySyncStatusRepository repository;

    public AbstractManagementDbInit(final EntitySyncStatusRepository repository) {
        this.repository = repository;
    }

    /**
     * Get the tables to synchronize
     * @return list of enums
     */
    protected abstract List<EntityNameEnum> getTablesToSync();

    /**
     * Inits management database with configured table names to sync
     */
    public void start() {
        List<EntitySyncStatus> createdStatuses = getTablesToSync().stream()
                .filter(entityNameEnum -> repository.countByEntityName(entityNameEnum) == 0)
                .map(this::createEntitySyncStatus)
                .map(status -> repository.save(status))
                .collect(Collectors.toList());

        String createdRows = createdStatuses.stream()
                .map(EntitySyncStatus::getEntityName)
                .map(Enum::name)
                .collect(Collectors.joining(","));

        log.info("Status created for tables: " + createdRows);
    }

    private EntitySyncStatus createEntitySyncStatus(final EntityNameEnum entityNameEnum) {
        EntitySyncStatus entitySyncStatus = new EntitySyncStatus();
        entitySyncStatus.setEntityName(entityNameEnum);
        return entitySyncStatus;
    }
}
