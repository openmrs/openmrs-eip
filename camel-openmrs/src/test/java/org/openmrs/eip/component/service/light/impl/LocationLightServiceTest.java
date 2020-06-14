package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class LocationLightServiceTest {

    @Mock
    private OpenmrsRepository<LocationLight> repository;

    private LocationLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new LocationLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        LocationLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedLocation(), result);
    }

    private LocationLight getExpectedLocation() {
        LocationLight location = new LocationLight();
        location.setCreator(1L);
        location.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        location.setName("[Default]");
        return location;
    }
}
