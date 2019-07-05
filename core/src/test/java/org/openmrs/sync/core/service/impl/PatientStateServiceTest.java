package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PatientStateServiceTest {

    @Mock
    private SyncEntityRepository<PatientState> repository;

    @Mock
    private EntityToModelMapper<PatientState, PatientStateModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PatientStateModel, PatientState> modelToEntityMapper;

    private PatientStateService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientStateService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PATIENT_STATE, service.getTableToSync());
    }
}
