package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowStateContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProgramWorkflowStateLightServiceTest {

    @Mock
    private OpenMrsRepository<ProgramWorkflowStateLight> repository;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    private LightService<ProgramWorkflowLight, ProgramWorkflowContext> programWorkflowService;

    private ProgramWorkflowStateLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new ProgramWorkflowStateLightService(repository, conceptService, programWorkflowService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        ProgramWorkflowStateContext programWorkflowContext = ProgramWorkflowStateContext.builder()
                .conceptUuid("concept")
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .workflowUuid("workflow")
                .workflowConceptUuid("workflowConcept")
                .workflowConceptClassUuid("workflowConceptClass")
                .workflowConceptDatatypeUuid("workflowConceptDatatype")
                .workflowProgramUuid("workflowProgram")
                .workflowProgramConceptUuid("workflowProgramConcept")
                .workflowProgramConceptClassUuid("workflowProgramConceptClass")
                .workflowProgramConceptDatatypeUuid("workflowProgramConceptDatatype")
                .build();
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
        ProgramWorkflowContext programContext = ProgramWorkflowContext.builder()
                .conceptUuid("workflowConcept")
                .conceptClassUuid("workflowConceptClass")
                .conceptDatatypeUuid("workflowConceptDatatype")
                .programUuid("workflowProgram")
                .programConceptUuid("workflowProgramConcept")
                .programConceptClassUuid("workflowProgramConceptClass")
                .programConceptDatatypeUuid("workflowProgramConceptDatatype")
                .build();
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(getConcept());
        when(programWorkflowService.getOrInit("workflow", programContext)).thenReturn(getProgramWorkflow());

        // When
        ProgramWorkflowStateLight result = service.getShadowEntity("UUID", programWorkflowContext);

        // Then
        assertEquals(getExpectedProgramWorkflow(), result);
    }

    private ProgramWorkflowStateLight getExpectedProgramWorkflow() {
        ProgramWorkflowStateLight programWorkflowState = new ProgramWorkflowStateLight();
        programWorkflowState.setUuid("UUID");
        programWorkflowState.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        programWorkflowState.setCreator(1L);
        programWorkflowState.setConcept(getConcept());
        programWorkflowState.setProgramWorkflow(getProgramWorkflow());
        return programWorkflowState;
    }

    private ProgramWorkflowLight getProgramWorkflow() {
        ProgramWorkflowLight programWorkflow = new ProgramWorkflowLight();
        programWorkflow.setUuid("programWorkflow");
        return programWorkflow;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
