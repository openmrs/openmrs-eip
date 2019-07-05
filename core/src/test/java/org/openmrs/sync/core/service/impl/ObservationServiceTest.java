package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ObservationServiceTest {

    @Mock
    private SyncEntityRepository<Observation> repository;

    @Mock
    private EntityToModelMapper<Observation, ObservationModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<ObservationModel, Observation> modelToEntityMapper;

    private ObservationService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ObservationService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.OBSERVATION, service.getTableToSync());
    }
}
