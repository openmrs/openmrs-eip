package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class EncounterServiceTest {

    @Mock
    private SyncEntityRepository<Encounter> repository;

    @Mock
    private EntityMapper<Encounter, EncounterModel> mapper;

    private EncounterService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new EncounterService(repository, mapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.ENCOUNTER, service.getTableToSync());
    }
}
