package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PatientProgram;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.PatientProgramModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PatientProgramServiceTest {

    @Mock
    private SyncEntityRepository<PatientProgram> repository;

    @Mock
    private EntityToModelMapper<PatientProgram, PatientProgramModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PatientProgramModel, PatientProgram> modelToEntityMapper;

    private PatientProgramService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientProgramService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PATIENT_PROGRAM, service.getTableToSync());
    }
}
