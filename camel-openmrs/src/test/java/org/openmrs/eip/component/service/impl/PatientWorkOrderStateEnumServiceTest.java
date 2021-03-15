package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.PatientStateModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.PatientState;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class PatientWorkOrderStateEnumServiceTest {

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
        Assert.assertEquals(TableToSyncEnum.PATIENT_STATE, service.getTableToSync());
    }
}
