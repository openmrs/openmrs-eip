package org.openmrs.sync.remote.management.init.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ManagementDbInitTest {

    @Mock
    private EntitySyncStatusRepository repository;

    private ManagementDbInit dbInit;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        dbInit = new ManagementDbInit(repository);
    }

    @Test
    public void getTablesToSync() {
        assertEquals(Arrays.asList(EntityNameEnum.values()), dbInit.getTablesToSync());
    }
}
