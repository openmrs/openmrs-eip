package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.EncounterDiagnosis;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.EncounterDiagnosisModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class EncounterDiagnosisServiceTest {

    @Mock
    private SyncEntityRepository<EncounterDiagnosis> repository;

    @Mock
    private EntityToModelMapper<EncounterDiagnosis, EncounterDiagnosisModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<EncounterDiagnosisModel, EncounterDiagnosis> modelToEntityMapper;

    private EncounterDiagnosisService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new EncounterDiagnosisService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.ENCOUNTER_DIAGNOSIS, service.getTableToSync());
    }
}
