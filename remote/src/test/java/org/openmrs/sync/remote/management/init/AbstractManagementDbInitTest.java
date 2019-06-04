package org.openmrs.sync.remote.management.init;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.management.entity.EntitySyncStatus;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;

import static org.mockito.Mockito.*;

public class AbstractManagementDbInitTest {

    @Mock
    private EntitySyncStatusRepository repository;

    private MockedManagementDbInit dbInit;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        dbInit = new MockedManagementDbInit(repository);
    }

    @Test
    public void start() {
        // Given
        EntitySyncStatus toSave = new EntitySyncStatus();
        toSave.setEntityName(EntityNameEnum.PERSON);

        when(repository.countByEntityName(EntityNameEnum.PERSON)).thenReturn(0L);
        when(repository.countByEntityName(EntityNameEnum.VISIT)).thenReturn(1L);
        when(repository.save(toSave)).thenReturn(toSave);

        // When
        dbInit.start();

        // Then
        verify(repository).save(any(EntitySyncStatus.class));
    }
}
