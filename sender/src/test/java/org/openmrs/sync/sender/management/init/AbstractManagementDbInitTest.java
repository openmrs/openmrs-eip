package org.openmrs.sync.sender.management.init;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.sender.management.entity.TableSyncStatus;
import org.openmrs.sync.sender.management.repository.TableSyncStatusRepository;

import static org.mockito.Mockito.*;

public class AbstractManagementDbInitTest {

    @Mock
    private TableSyncStatusRepository tableSyncStatusRepository;

    private MockedManagementDbInit dbInit;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        dbInit = new MockedManagementDbInit(tableSyncStatusRepository);
    }

    @Test
    public void start() {
        // Given
        TableSyncStatus toSave = new TableSyncStatus();
        toSave.setTableToSync(TableToSyncEnum.PERSON);

        when(tableSyncStatusRepository.countByTableToSync(TableToSyncEnum.PERSON)).thenReturn(0L);
        when(tableSyncStatusRepository.countByTableToSync(TableToSyncEnum.VISIT)).thenReturn(1L);
        when(tableSyncStatusRepository.save(toSave)).thenReturn(toSave);

        // When
        dbInit.start();

        // Then
        verify(tableSyncStatusRepository).save(any(TableSyncStatus.class));
    }
}
