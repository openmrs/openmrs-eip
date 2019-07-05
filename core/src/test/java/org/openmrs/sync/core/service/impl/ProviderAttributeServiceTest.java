package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ProviderAttribute;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

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
