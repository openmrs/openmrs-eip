package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class LocationLightServiceTest {

    @Mock
    private OpenMrsRepository<LocationLight> repository;

    private LocationLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new LocationLightService(repository);
    }

    @Test
    public void getShadowEntity() {
        assertEquals(getExpectedLocation(), service.getShadowEntity("uuid"));
    }

    private LocationLight getExpectedLocation() {
        LocationLight location = new LocationLight();
        location.setUuid("uuid");
        location.setCreator(1L);
        location.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        location.setName("[Default]");
        return location;
    }
}
