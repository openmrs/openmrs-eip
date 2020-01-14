package org.openmrs.sync.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;

import static org.junit.Assert.assertNotNull;

public class VisitAttributeTypeLightServiceTest {

    @Mock
    private OpenmrsRepository<VisitAttributeTypeLight> repository;

    private VisitAttributeTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitAttributeTypeLightService(repository);
    }

    @Test
    public void createEntity() {
        assertNotNull(service.createEntity());
    }
}
