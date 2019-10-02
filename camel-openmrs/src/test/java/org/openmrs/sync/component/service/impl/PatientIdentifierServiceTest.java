package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.PatientIdentifier;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.PatientIdentifierModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class PatientIdentifierServiceTest {

    @Mock
    private SyncEntityRepository<PatientIdentifier> repository;

    @Mock
    private EntityToModelMapper<PatientIdentifier, PatientIdentifierModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<PatientIdentifierModel, PatientIdentifier> modelToEntityMapper;

    private PatientIdentifierService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientIdentifierService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PATIENT_IDENTIFIER, service.getTableToSync());
    }
}
