package org.openmrs.sync.component.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.ProviderAttribute;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.AttributeModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ProviderAttributeServiceTest {

    @Mock
    private SyncEntityRepository<ProviderAttribute> repository;

    @Mock
    private EntityToModelMapper<ProviderAttribute, AttributeModel> entityToModelMapper;

    @Mock
    private ModelToEntityMapper<AttributeModel, ProviderAttribute> modelToEntityMapper;

    private ProviderAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProviderAttributeService(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PROVIDER_ATTRIBUTE, service.getTableToSync());
    }
}
