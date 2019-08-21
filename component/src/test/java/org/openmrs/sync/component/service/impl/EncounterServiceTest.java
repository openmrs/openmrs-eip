package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Encounter;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.EncounterModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class EncounterServiceTest {

    @Mock
    private SyncEntityRepository<Encounter> repository;

    @Mock
    private EntityToModelMapper<Encounter, EncounterModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<EncounterModel, Encounter> modelToEntityMapper;

    private EncounterService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new EncounterService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.ENCOUNTER, service.getTableToSync());
    }
}
