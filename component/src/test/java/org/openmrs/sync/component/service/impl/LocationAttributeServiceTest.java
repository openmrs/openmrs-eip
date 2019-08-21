package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.LocationAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.common.model.sync.AttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

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
