package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import static org.junit.Assert.assertNotNull;

public class ProviderAttributeTypeLightServiceTest {

    @Mock
    private OpenMrsRepository<ProviderAttributeTypeLight> repository;

    private ProviderAttributeTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProviderAttributeTypeLightService(repository);
    }

    @Test
    public void createEntity() {
        assertNotNull(service.createEntity());
    }
}
