package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.PatientProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PatientProgramLightServiceTest {

    @Mock
    private OpenMrsRepository<PatientProgramLight> repository;

    @Mock
    private LightServiceNoContext<PatientLight> patientService;

    @Mock
    private LightService<ProgramLight, ProgramContext> programService;

    private PatientProgramLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new PatientProgramLightService(repository, patientService, programService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        PatientProgramContext patientProgramContext = PatientProgramContext.builder()
                .programUuid("program")
                .programConceptUuid("concept")
                .programConceptClassUuid("conceptClass")
                .programConceptDatatypeUuid("conceptDatatype")
                .patientUuid("patient")
                .build();
        ProgramContext programContext = ProgramContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .conceptUuid("concept")
                .build();
        when(patientService.getOrInit("patient")).thenReturn(getPatient());
        when(programService.getOrInit("program", programContext)).thenReturn(getProgram());

        // When
        PatientProgramLight result = service.getShadowEntity("UUID", patientProgramContext);

        // Then
        assertEquals(getExpectedPatientProgram(), result);
    }

    private PatientProgramLight getExpectedPatientProgram() {
        PatientProgramLight patientProgram = new PatientProgramLight();
        patientProgram.setUuid("UUID");
        patientProgram.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        patientProgram.setCreator(1L);
        patientProgram.setPatient(getPatient());
        patientProgram.setProgram(getProgram());
        return patientProgram;
    }

    private ProgramLight getProgram() {
        ProgramLight program = new ProgramLight();
        program.setUuid("program");
        return program;
    }

    private PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("patient");
        return patient;
    }
}
