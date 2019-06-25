package org.openmrs.sync.remote.management.init.impl;

import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.remote.management.init.AbstractManagementDbInit;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ManagementDbInit extends AbstractManagementDbInit {

    public ManagementDbInit(final TableSyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<TableToSyncEnum> getTablesToSync() {
        return Arrays.asList(TableToSyncEnum.values());
    }
}
