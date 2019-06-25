package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ObservationServiceTest {

    @Mock
    private SyncEntityRepository<Observation> repository;

    @Mock
    private EntityMapper<Observation, ObservationModel> mapper;

    private ObservationService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ObservationService(repository, mapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.OBSERVATION, service.getTableToSync());
    }
}
