package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import static org.junit.Assert.assertNotNull;

public class ConceptAttributeTypeLightServiceTest {

    @Mock
    private OpenMrsRepository<ConceptAttributeTypeLight> repository;

    private ConceptAttributeTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConceptAttributeTypeLightService(repository);
    }

    @Test
    public void createEntity() {
        assertNotNull(service.createEntity());
    }
}
