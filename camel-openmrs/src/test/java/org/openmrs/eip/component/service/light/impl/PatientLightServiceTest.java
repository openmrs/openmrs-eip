package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PatientLightServiceTest {

    @Mock
    private OpenmrsRepository<PatientLight> repository;

    private PatientLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientLightService(repository);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        String uuid = "uuid";

        // When
        PatientLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedLocation(), result);
    }

    private PatientLight getExpectedLocation() {
        PatientLight patient = new PatientLight();
        patient.setCreator(1L);
        patient.setPatientCreator(1L);
        patient.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patient.setPatientDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patient.setAllergyStatus("[Default]");
        return patient;
    }
}
