package org.openmrs.sync.remote.management.init;

import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;

import java.util.Arrays;
import java.util.List;

public class MockedManagementDbInit extends AbstractManagementDbInit {

    public MockedManagementDbInit(final EntitySyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<EntityNameEnum> getTablesToSync() {
        return Arrays.asList(EntityNameEnum.PERSON, EntityNameEnum.VISIT);
    }
}
