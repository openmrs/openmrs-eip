package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Patient;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.PatientModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PatientServiceTest {

    @Mock
    private SyncEntityRepository<Patient> repository;

    @Mock
    private EntityToModelMapper<Patient, PatientModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PatientModel, Patient> modelToEntityMapper;

    private PatientService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PATIENT, service.getTableToSync());
    }
}
