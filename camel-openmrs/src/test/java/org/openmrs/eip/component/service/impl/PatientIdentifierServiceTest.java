package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.PatientIdentifier;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

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
        Assert.assertEquals(TableToSyncEnum.PATIENT_IDENTIFIER, service.getTableToSync());
    }
}
