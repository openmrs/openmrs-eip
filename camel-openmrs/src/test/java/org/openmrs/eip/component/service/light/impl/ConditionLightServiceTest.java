package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.ConditionLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConditionLightServiceTest {

    @Mock
    private OpenmrsRepository<ConditionLight> repository;

    @Mock
    private LightService<PatientLight> patientService;

    private ConditionLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ConditionLightService(repository, patientService);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        when(patientService.getOrInitPlaceholderEntity()).thenReturn(getPatient());
        String uuid = "uuid";

        // When
        ConditionLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedCondition(), result);
    }

    private ConditionLight getExpectedCondition() {
        ConditionLight condition = new ConditionLight();
        condition.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        condition.setCreator(1L);
        condition.setPatient(getPatient());
        condition.setClinicalStatus("[Default]");
        return condition;
    }

    private PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("PLACEHOLDER_PATIENT");
        return patient;
    }
}
