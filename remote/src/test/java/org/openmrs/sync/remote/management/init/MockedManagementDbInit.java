package org.openmrs.sync.remote.management.init;

import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;

import java.util.Arrays;
import java.util.List;

public class MockedManagementDbInit extends AbstractManagementDbInit {

    public MockedManagementDbInit(final TableSyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<TableNameEnum> getTablesToSync() {
        return Arrays.asList(TableNameEnum.PERSON, TableNameEnum.PATIENT);
    }
}
