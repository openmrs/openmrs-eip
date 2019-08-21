package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Observation;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.ObservationModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

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
