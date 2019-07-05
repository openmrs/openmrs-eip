package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Location;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.LocationModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class LocationServiceTest {

    @Mock
    private SyncEntityRepository<Location> repository;

    @Mock
    private EntityToModelMapper<Location, LocationModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<LocationModel, Location> modelToEntityMapper;

    private LocationService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new LocationService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.LOCATION, service.getTableToSync());
    }
}
