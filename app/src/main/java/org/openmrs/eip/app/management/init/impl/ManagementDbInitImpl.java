package org.openmrs.eip.app.management.init.impl;

import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.app.management.init.AbstractManagementDbInit;
import org.openmrs.eip.app.management.repository.TableSyncStatusRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Profile(SyncProfiles.SENDER)
@Component
public class ManagementDbInitImpl extends AbstractManagementDbInit {

    public ManagementDbInitImpl(final TableSyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<TableToSyncEnum> getTablesToSync() {
        return Arrays.asList(TableToSyncEnum.values());
    }
}
