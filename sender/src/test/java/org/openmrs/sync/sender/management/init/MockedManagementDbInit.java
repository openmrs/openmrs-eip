package org.openmrs.sync.sender.management.init;

import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.sender.management.repository.TableSyncStatusRepository;

import java.util.Arrays;
import java.util.List;

public class MockedManagementDbInit extends AbstractManagementDbInit {

    public MockedManagementDbInit(final TableSyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<TableToSyncEnum> getTablesToSync() {
        return Arrays.asList(TableToSyncEnum.PERSON, TableToSyncEnum.VISIT);
    }
}
