package org.openmrs.sync.core.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ProviderAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.TableToSyncEnum;

import static org.junit.Assert.assertEquals;

public class ProviderAttributeServiceTest {

    @Mock
    private SyncEntityRepository<ProviderAttribute> repository;

    @Mock
    private EntityMapper<ProviderAttribute, AttributeModel> mapper;

    private ProviderAttributeService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProviderAttributeService(repository, mapper);
    }

    @Test
    public void getTableToSync() {
        assertEquals(TableToSyncEnum.PROVIDER_ATTRIBUTE, service.getTableToSync());
    }
}
