package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class EncounterTypeLightServiceTest {

    @Mock
    private OpenmrsRepository<EncounterTypeLight> repository;

    private EncounterTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new EncounterTypeLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        EncounterTypeLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedEncounterType(), result);
    }

    private EncounterTypeLight getExpectedEncounterType() {
        EncounterTypeLight encounterType = new EncounterTypeLight();
        encounterType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        encounterType.setCreator(1L);
        encounterType.setName("[Default] - " + "uuid");
        return encounterType;
    }
}
