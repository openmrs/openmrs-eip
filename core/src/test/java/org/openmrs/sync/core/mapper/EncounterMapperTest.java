package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.sync.core.entity.Encounter;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.EncounterModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EncounterMapperTest extends AbstractMapperTest {

    @Mock
    private LightServiceNoContext<EncounterTypeLight> encounterTypeService;

    @Mock
    private LightServiceNoContext<PatientLight> patientService;

    @Mock
    private LightServiceNoContext<LocationLight> locationService;

    @Mock
    private LightServiceNoContext<FormLight> formService;

    @Mock
    private LightService<VisitLight, VisitContext> visitService;

    @Mock
    private LightServiceNoContext<UserLight> userService;

    @InjectMocks
    private EncounterMapperImpl mapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        Encounter ety = getEncounterEty();

        // When
        EncounterModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        VisitContext visitContext = VisitContext.builder()
                .visitTypeUuid("visitVisitType")
                .patientUuid("visitPatient")
                .build();

        EncounterModel model = getEncounterModel();
        when(encounterTypeService.getOrInit("encounterType")).thenReturn(getEncounterType());
        when(patientService.getOrInit("patient")).thenReturn(getPatient("patient"));
        when(locationService.getOrInit("location")).thenReturn(getLocation());
        when(formService.getOrInit("form")).thenReturn(getForm());
        when(visitService.getOrInit("visit", visitContext)).thenReturn(getVisit());
        when(userService.getOrInit("user")).thenReturn(getUser());

        // When
        Encounter result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final Encounter ety, final EncounterModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getEncounterDatetime(), result.getEncounterDatetime());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getForm().getUuid(), result.getFormUuid());
        assertEquals(ety.getPatient().getUuid(), result.getPatientUuid());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
        assertEquals(ety.getLocation().getUuid(), result.getLocationUuid());
        assertEquals(ety.getEncounterType().getUuid(), result.getEncounterTypeUuid());
        assertEquals(ety.getVisit().getUuid(), result.getVisitUuid());
        assertEquals(ety.getVisit().getVisitType().getUuid(), result.getVisitVisitTypeUuid());
        assertEquals(ety.getVisit().getPatient().getUuid(), result.getVisitPatientUuid());
    }

    private Encounter getEncounterEty() {
        Encounter encounter = new Encounter();

        encounter.setUuid("encounter");
        encounter.setDateCreated(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        encounter.setDateChanged(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        encounter.setVoided(true);
        encounter.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        encounter.setVoidReason("reason");
        encounter.setEncounterDatetime(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        encounter.setVoidedBy(getUser());
        encounter.setCreator(getUser());
        encounter.setForm(getForm());
        encounter.setPatient(getPatient("visitPatient"));
        encounter.setChangedBy(getUser());
        encounter.setLocation(getLocation());
        encounter.setVisit(getVisit());
        encounter.setEncounterType(getEncounterType());

        return encounter;
    }

    private void assertResult(final EncounterModel model, final Encounter result) {
        assertEquals(model.getVisitUuid(), result.getVisit().getUuid());
        assertEquals(model.getVisitPatientUuid(), result.getVisit().getPatient().getUuid());
        assertEquals(model.getVisitVisitTypeUuid(), result.getVisit().getVisitType().getUuid());
        assertEquals(model.getFormUuid(), result.getForm().getUuid());
        assertEquals(model.getEncounterTypeUuid(), result.getEncounterType().getUuid());
        assertEquals(model.getPatientUuid(), result.getPatient().getUuid());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getLocationUuid(), result.getLocation().getUuid());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
        assertEquals(model.getEncounterDatetime(), result.getEncounterDatetime());
    }

    private EncounterModel getEncounterModel() {
        EncounterModel encounterModel = new EncounterModel();

        encounterModel.setFormUuid("form");
        encounterModel.setEncounterTypeUuid("encounterType");
        encounterModel.setPatientUuid("patient");
        encounterModel.setCreatorUuid("user");
        encounterModel.setLocationUuid("location");
        encounterModel.setVoidedByUuid("user");
        encounterModel.setChangedByUuid("user");
        encounterModel.setVisitVisitTypeUuid("visitVisitType");
        encounterModel.setVisitPatientUuid("visitPatient");
        encounterModel.setVisitUuid("visit");
        encounterModel.setUuid("encounter");
        encounterModel.setDateCreated(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        encounterModel.setDateChanged(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        encounterModel.setVoided(true);
        encounterModel.setDateVoided(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        encounterModel.setVoidReason("reason");
        encounterModel.setEncounterDatetime(LocalDateTime.of(2014,Month.JANUARY, 1, 10, 11));

        return encounterModel;
    }
}
