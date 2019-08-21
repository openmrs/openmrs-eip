package org.openmrs.sync.app.management.init.impl;

import org.openmrs.sync.app.SyncProfiles;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.app.management.init.AbstractManagementDbInit;
import org.openmrs.sync.app.management.repository.TableSyncStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Profile(SyncProfiles.SENDER)
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
