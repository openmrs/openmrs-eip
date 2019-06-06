package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.impl.PatientLightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PatientLightServiceTest {

    @Mock
    private OpenMrsRepository<PatientLight> repository;

    private PatientLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientLightService(repository);
    }

    @Test
    public void getFakeEntity() {
        assertEquals(getExpectedLocation(), service.getFakeEntity("uuid"));
    }

    private PatientLight getExpectedLocation() {
        PatientLight patient = new PatientLight();
        patient.setUuid("uuid");
        patient.setCreator(1L);
        patient.setPatientCreator(1L);
        patient.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patient.setPatientDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patient.setAllergyStatus("Default");
        return patient;
    }
}
