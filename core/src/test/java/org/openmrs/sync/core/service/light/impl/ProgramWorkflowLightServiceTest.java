package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProgramWorkflowLightServiceTest {

    @Mock
    private OpenMrsRepository<ProgramWorkflowLight> repository;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    private LightService<ProgramLight, ProgramContext> programService;

    private ProgramWorkflowLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProgramWorkflowLightService(repository, conceptService, programService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        ProgramWorkflowContext programWorkflowContext = ProgramWorkflowContext.builder()
                .conceptUuid("concept")
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .programUuid("program")
                .programConceptUuid("programConcept")
                .programConceptClassUuid("programConceptClass")
                .programConceptDatatypeUuid("programConceptDatatype")
                .build();
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
        ProgramContext programContext = ProgramContext.builder()
                .conceptUuid("programConcept")
                .conceptClassUuid("programConceptClass")
                .conceptDatatypeUuid("programConceptDatatype")
                .build();
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(getConcept());
        when(programService.getOrInit("program", programContext)).thenReturn(getProgram());

        // When
        ProgramWorkflowLight result = service.getShadowEntity("UUID", programWorkflowContext);

        // Then
        assertEquals(getExpectedProgramWorkflow(), result);
    }

    private ProgramWorkflowLight getExpectedProgramWorkflow() {
        ProgramWorkflowLight programWorkflow = new ProgramWorkflowLight();
        programWorkflow.setUuid("UUID");
        programWorkflow.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        programWorkflow.setCreator(1L);
        programWorkflow.setConcept(getConcept());
        programWorkflow.setProgram(getProgram());
        return programWorkflow;
    }

    private ProgramLight getProgram() {
        ProgramLight program = new ProgramLight();
        program.setUuid("program");
        return program;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
