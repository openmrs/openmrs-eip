package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.LocationAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class LocationAttributeServiceTest {

    @Mock
    private SyncEntityRepository<LocationAttribute> repository;

    @Mock
    private EntityToModelMapper<LocationAttribute, AttributeModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<AttributeModel, LocationAttribute> modelToEntityMapper;

    private LocationAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new LocationAttributeService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.LOCATION_ATTRIBUTE, service.getTableToSync());
    }
}
