package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PatientStateServiceTest {

    @Mock
    private SyncEntityRepository<PatientState> repository;

    @Mock
    private EntityMapper<PatientState, PatientStateModel> mapper;

    private PatientStateService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientStateService(repository, mapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PATIENT_STATE, service.getTableToSync());
    }
}
