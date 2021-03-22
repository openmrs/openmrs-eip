package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.PatientProgramLight;
import org.openmrs.eip.component.entity.light.ProgramLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PatientProgramLightServiceTest {

    @Mock
    private OpenmrsRepository<PatientProgramLight> repository;

    @Mock
    private LightService<PatientLight> patientService;

    @Mock
    private LightService<ProgramLight> programService;

    private PatientProgramLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientProgramLightService(repository, patientService, programService);
    }

    @Test
    public void createPlaceholderEntity() {
        // Given
        when(patientService.getOrInitPlaceholderEntity()).thenReturn(getPatient());
        when(programService.getOrInitPlaceholderEntity()).thenReturn(getProgram());
        String uuid = "uuid";

        // When
        PatientProgramLight result = service.createPlaceholderEntity(uuid);

        // Then
        assertEquals(getExpectedPatientProgram(), result);
    }

    private PatientProgramLight getExpectedPatientProgram() {
        PatientProgramLight patientProgram = new PatientProgramLight();
        patientProgram.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patientProgram.setCreator(1L);
        patientProgram.setPatient(getPatient());
        patientProgram.setProgram(getProgram());
        return patientProgram;
    }

    private ProgramLight getProgram() {
        ProgramLight program = new ProgramLight();
        program.setUuid("PLACEHOLDER_PROGRAM");
        return program;
    }

    private PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("PLACEHOLDER_PATIENT");
        return patient;
    }
}
