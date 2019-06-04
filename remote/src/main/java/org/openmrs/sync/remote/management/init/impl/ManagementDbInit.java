package org.openmrs.sync.remote.management.init.impl;

import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.init.AbstractManagementDbInit;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ManagementDbInit extends AbstractManagementDbInit {

    public ManagementDbInit(final EntitySyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<EntityNameEnum> getTablesToSync() {
        return Arrays.asList(EntityNameEnum.values());
    }
}
