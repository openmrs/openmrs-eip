package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.Location;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.LocationModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

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
