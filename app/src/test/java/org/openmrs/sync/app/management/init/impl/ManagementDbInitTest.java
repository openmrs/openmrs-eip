package org.openmrs.sync.app.management.init.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.app.management.repository.TableSyncStatusRepository;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ManagementDbInitTest {

    @Mock
    private TableSyncStatusRepository repository;

    private ManagementDbInitImpl dbInit;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        dbInit = new ManagementDbInitImpl(repository);
    }

    @Test
    public void getTablesToSync() {
        assertEquals(Arrays.asList(TableToSyncEnum.values()), dbInit.getTablesToSync());
    }
}
