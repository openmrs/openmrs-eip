package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.impl.VisitTypeLightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class VisitTypeLightServiceTest {

    @Mock
    private OpenMrsRepository<VisitTypeLight> repository;

    private VisitTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitTypeLightService(repository);
    }

    @Test
    public void getFakeEntity() {
        assertEquals(getExpectedLocation(), service.getFakeEntity("uuid"));
    }

    private VisitTypeLight getExpectedLocation() {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid("uuid");
        visitType.setCreator(1L);
        visitType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        visitType.setName("Default");
        return visitType;
    }
}
