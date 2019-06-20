package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.PatientProgram;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.PatientProgramModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PatientProgramMapperTest extends AbstractMapperTest {

    @Mock
    protected LightServiceNoContext<PatientLight> patientService;

    @Mock
    protected LightService<ProgramLight, ProgramContext> programService;

    @Mock
    protected LightServiceNoContext<LocationLight> locationService;

    @Mock
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    protected LightServiceNoContext<UserLight> userService;

    @InjectMocks
    private PatientProgramMapperImpl mapper;

    private PatientLight patient = initBaseModel(PatientLight.class, "patient");
    private ProgramLight program = initBaseModel(ProgramLight.class, "program");
    private LocationLight location = initBaseModel(LocationLight.class, "location");
    private ConceptLight outcomeConcept = initBaseModel(ConceptLight.class, "outcomeConcept");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        PatientProgram ety = getPatientProgramEty();

        // When
        PatientProgramModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        PatientProgramModel model = getPatientProgramModel();
        when(patientService.getOrInit("patient")).thenReturn(patient);
        when(programService.getOrInit("program", getProgramContext())).thenReturn(program);
        when(locationService.getOrInit("location")).thenReturn(location);
        when(conceptService.getOrInit("outcomeConcept", getConceptContext())).thenReturn(outcomeConcept);
        when(userService.getOrInit("user")).thenReturn(user);

        // When
        PatientProgram result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(PatientProgram ety, PatientProgramModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getProgram().getUuid(), result.getProgramUuid());
        assertEquals(ety.getPatient().getUuid(), result.getPatientUuid());
        assertEquals(ety.getDateEnrolled(), result.getDateEnrolled());
        assertEquals(ety.getDateCompleted(), result.getDateCompleted());
        assertEquals(ety.getLocation().getUuid(), result.getLocationUuid());
        assertEquals(ety.getOutcomeConcept().getUuid(), result.getOutcomeConceptUuid());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
    }

    private PatientProgram getPatientProgramEty() {
        PatientProgram ety = new PatientProgram();
        ety.setUuid("patientProgram");
        ety.setProgram(program);
        ety.setPatient(patient);
        ety.setDateEnrolled(LocalDateTime.of(2013, Month.JANUARY, 1, 0, 0));
        ety.setDateCompleted(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0));
        ety.setLocation(location);
        ety.setOutcomeConcept(outcomeConcept);
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

    private void assertResult(final PatientProgramModel model,
                              final PatientProgram result) {
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getProgramUuid(), result.getProgram().getUuid());
        assertEquals(model.getPatientUuid(), result.getPatient().getUuid());
        assertEquals(model.getLocationUuid(), result.getLocation().getUuid());
        assertEquals(model.getOutcomeConceptUuid(), result.getOutcomeConcept().getUuid());
        assertEquals(model.getDateEnrolled(), result.getDateEnrolled());
        assertEquals(model.getDateCompleted(), result.getDateCompleted());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
    }

    private PatientProgramModel getPatientProgramModel() {
        PatientProgramModel model = new PatientProgramModel();
        model.setUuid("person");
        model.setProgramUuid("program");
        model.setProgramConceptUuid("programConcept");
        model.setProgramConceptClassUuid("programConceptClass");
        model.setProgramConceptDatatypeUuid("programConceptDatatype");
        model.setPatientUuid("patient");
        model.setLocationUuid("location");
        model.setOutcomeConceptUuid("outcomeConcept");
        model.setOutcomeConceptClassUuid("outcomeConceptClass");
        model.setOutcomeConceptDatatypeUuid("outcomeConceptDatatype");
        model.setDateEnrolled(LocalDateTime.of(2013, Month.JANUARY, 1, 0, 0));
        model.setDateCompleted(LocalDateTime.of(2014, Month.JANUARY, 1, 0, 0));
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

    private ProgramContext getProgramContext() {
        return ProgramContext.builder()
                .conceptUuid("programConcept")
                .conceptClassUuid("programConceptClass")
                .conceptDatatypeUuid("programConceptDatatype")
                .build();
    }

    private ConceptContext getConceptContext() {
        return ConceptContext.builder()
                .conceptClassUuid("outcomeConceptClass")
                .conceptDatatypeUuid("outcomeConceptDatatype")
                .build();
    }
}
