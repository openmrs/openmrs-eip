package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PatientState;
import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PatientStateModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.PatientProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramWorkflowStateContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PatientStateMapperTest extends AbstractMapperTest {

    @Mock
    protected LightService<PatientProgramLight, PatientProgramContext> patientProgramService;

    @Mock
    protected LightService<ProgramWorkflowStateLight, ProgramWorkflowStateContext> programWorkflowStateService;

    @Mock
    protected LightServiceNoContext<UserLight> userService;

    @InjectMocks
    private PatientStateMapperImpl mapper;

    private PatientProgramLight patientProgram = initBaseModel(PatientProgramLight.class, "patientProgram");
    private ProgramWorkflowStateLight workflowState = initBaseModel(ProgramWorkflowStateLight.class, "workflowState");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        PatientState ety = getPatientStateEty();

        // When
        PatientStateModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        PatientStateModel model = getPatientStateModel();
        when(patientProgramService.getOrInit("patientProgram", getPatientProgramContext())).thenReturn(patientProgram);
        when(programWorkflowStateService.getOrInit("workflowState", getWorkflowStateContext())).thenReturn(workflowState);
        when(userService.getOrInit("user")).thenReturn(user);

        // When
        PatientState result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(PatientState ety, PatientStateModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getPatientProgram().getUuid(), result.getPatientProgramUuid());
        assertEquals(ety.getState().getUuid(), result.getStateUuid());
        assertEquals(ety.getStartDate(), result.getStartDate());
        assertEquals(ety.getEndDate(), result.getEndDate());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
    }

    private PatientState getPatientStateEty() {
        PatientState ety = new PatientState();
        ety.setUuid("PatientState");
        ety.setPatientProgram(patientProgram);
        ety.setState(workflowState);
        ety.setStartDate(LocalDate.of(2013, Month.JANUARY, 1));
        ety.setEndDate(LocalDate.of(2014, Month.JANUARY, 1));
        ety.setCreator(user);
        ety.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        ety.setChangedBy(user);
        ety.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        ety.setVoided(true);
        ety.setVoidedBy(user);
        ety.setDateVoided(LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0));
        ety.setVoidReason("voided");
        return ety;
    }

    private void assertResult(final PatientStateModel model,
                              final PatientState result) {
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getPatientProgramUuid(), result.getPatientProgram().getUuid());
        assertEquals(model.getStateUuid(), result.getState().getUuid());
        assertEquals(model.getStartDate(), result.getStartDate());
        assertEquals(model.getEndDate(), result.getEndDate());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
    }

    private PatientStateModel getPatientStateModel() {
        PatientStateModel model = new PatientStateModel();
        model.setUuid("person");
        model.setPatientProgramUuid("patientProgram");
        model.setPatientProgramProgramUuid("program");
        model.setPatientProgramProgramConceptUuid("programConcept");
        model.setPatientProgramProgramConceptClassUuid("programConceptClass");
        model.setPatientProgramProgramConceptDatatypeUuid("programConceptDatatype");
        model.setPatientProgramPatientUuid("patient");
        model.setStateUuid("workflowState");
        model.setStateConceptUuid("concept");
        model.setStateConceptClassUuid("conceptClass");
        model.setStateConceptDatatypeUuid("conceptDatatype");
        model.setStateWorkflowUuid("workflow");
        model.setStateWorkflowConceptUuid("workflowConcept");
        model.setStateWorkflowConceptClassUuid("workflowConceptClass");
        model.setStateWorkflowConceptDatatypeUuid("workflowConceptDatatype");
        model.setStateWorkflowProgramUuid("workflowProgram");
        model.setStateWorkflowProgramConceptUuid("workflowProgramConcept");
        model.setStateWorkflowProgramConceptClassUuid("workflowProgramConceptClass");
        model.setStateWorkflowProgramConceptDatatypeUuid("workflowProgramConceptDatatype");
        model.setStartDate(LocalDate.of(2013, Month.JANUARY, 1));
        model.setEndDate(LocalDate.of(2014, Month.JANUARY, 1));
        model.setCreatorUuid("user");
        model.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        model.setChangedByUuid("user");
        model.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        model.setVoided(true);
        model.setVoidedByUuid("user");
        model.setDateVoided(LocalDateTime.of(1012, Month.JANUARY, 1, 0, 0));
        model.setVoidReason("voided");
        return model;
    }

    private PatientProgramContext getPatientProgramContext() {
        return PatientProgramContext.builder()
                .programUuid("program")
                .programConceptUuid("programConcept")
                .programConceptClassUuid("programConceptClass")
                .programConceptDatatypeUuid("programConceptDatatype")
                .patientUuid("patient")
                .build();
    }

    private ProgramWorkflowStateContext getWorkflowStateContext() {
        return ProgramWorkflowStateContext.builder()
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
    }
}
