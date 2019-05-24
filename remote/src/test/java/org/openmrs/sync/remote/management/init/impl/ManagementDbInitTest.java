package org.openmrs.sync.remote.management.init.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ManagementDbInitTest {

    @Mock
    private TableSyncStatusRepository repository;

    private ManagementDbInit dbInit;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        dbInit = new ManagementDbInit(repository);
    }

    @Test
    public void getTablesToSync() {
        assertEquals(Arrays.asList(TableNameEnum.values()), dbInit.getTablesToSync());
    }
}
