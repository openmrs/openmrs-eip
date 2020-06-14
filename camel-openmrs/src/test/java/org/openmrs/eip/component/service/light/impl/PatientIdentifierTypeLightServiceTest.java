package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.PatientIdentifierTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PatientIdentifierTypeLightServiceTest {

    @Mock
    private OpenmrsRepository<PatientIdentifierTypeLight> repository;

    private PatientIdentifierTypeLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientIdentifierTypeLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        PatientIdentifierTypeLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedPatientIdentifierType(), result);
    }

    private PatientIdentifierTypeLight getExpectedPatientIdentifierType() {
        PatientIdentifierTypeLight location = new PatientIdentifierTypeLight();
        location.setCreator(1L);
        location.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        location.setName("[Default]");
        return location;
    }
}
