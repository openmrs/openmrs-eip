package org.openmrs.sync.remote.management.init.impl;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.remote.management.init.AbstractManagementDbInit;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ManagementDbInit extends AbstractManagementDbInit {

    public ManagementDbInit(final TableSyncStatusRepository repository) {
        super(repository);
    }

    @Override
    protected List<TableNameEnum> getTablesToSync() {
        return Arrays.asList(TableNameEnum.values());
    }
}
