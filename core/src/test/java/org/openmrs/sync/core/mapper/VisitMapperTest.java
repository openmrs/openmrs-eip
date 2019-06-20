package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Visit;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.VisitModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class VisitMapperTest extends AbstractMapperTest {

    @Mock
    protected LightServiceNoContext<UserLight> userService;

    @Mock
    protected LightServiceNoContext<VisitTypeLight> visitTypeService;

    @Mock
    protected LightServiceNoContext<LocationLight> locationService;

    @Mock
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    protected LightServiceNoContext<PatientLight> patientService;

    @InjectMocks
    private VisitMapperImpl mapper;

    private ConceptLight concept = initBaseModel(ConceptLight.class, "concept");
    private LocationLight location = initBaseModel(LocationLight.class, "location");
    private VisitTypeLight visitType = initBaseModel(VisitTypeLight.class, "visitType");
    private PatientLight patient = initBaseModel(PatientLight.class, "patient");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        Visit ety = getVisitEty();

        // When
        VisitModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
     public void modelToEntity() {
        // Given
        VisitModel model = getVisitModel();
        when(conceptService.getOrInit("concept", getConceptContext())).thenReturn(concept);
        when(userService.getOrInit("user")).thenReturn(user);
        when(locationService.getOrInit("location")).thenReturn(location);
        when(visitTypeService.getOrInit("visitType")).thenReturn(visitType);
        when(patientService.getOrInit("patient")).thenReturn(patient);

        // When
        Visit result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final Visit ety, final VisitModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getDateStarted(), result.getDateStarted());
        assertEquals(ety.getDateStopped(), result.getDateStopped());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getPatient().getUuid(), result.getPatientUuid());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
        assertEquals(ety.getLocation().getUuid(), result.getLocationUuid());
        assertEquals(ety.getIndicationConcept().getUuid(), result.getIndicationConceptUuid());
        assertEquals(ety.getVisitType().getUuid(), result.getVisitTypeUuid());
    }

    private Visit getVisitEty() {
        Visit visit = new Visit();
        visit.setUuid("visit");
        visit.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 10, 10));
        visit.setDateChanged(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 10));
        visit.setVoided(true);
        visit.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 10));
        visit.setVoidReason("reason");
        visit.setDateStarted(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        visit.setDateStopped(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 15));
        visit.setVoidedBy(user);
        visit.setCreator(user);
        visit.setPatient(patient);
        visit.setChangedBy(user);
        visit.setLocation(location);
        visit.setIndicationConcept(concept);
        visit.setVisitType(visitType);

        return visit;
    }

    private void assertResult(final VisitModel model, final Visit result) {
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
        assertEquals(model.getDateStarted(), result.getDateStarted());
        assertEquals(model.getDateStopped(), result.getDateStopped());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getPatientUuid(), result.getPatient().getUuid());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getLocationUuid(), result.getLocation().getUuid());
        assertEquals(model.getIndicationConceptUuid(), result.getIndicationConcept().getUuid());
        assertEquals(model.getVisitTypeUuid(), result.getVisitType().getUuid());
    }

    private VisitModel getVisitModel() {
        VisitModel visitModel = new VisitModel();

        visitModel.setIndicationConceptUuid("concept");
        visitModel.setIndicationConceptClassUuid("conceptClass");
        visitModel.setIndicationConceptDatatypeUuid("conceptDatatype");
        visitModel.setPatientUuid("patient");
        visitModel.setCreatorUuid("user");
        visitModel.setLocationUuid("location");
        visitModel.setVoidedByUuid("user");
        visitModel.setChangedByUuid("user");
        visitModel.setVisitTypeUuid("visitType");
        visitModel.setUuid("visit");
        visitModel.setDateStarted(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        visitModel.setDateStopped(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 15));
        visitModel.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 10, 10));
        visitModel.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 10));
        visitModel.setVoided(true);
        visitModel.setDateVoided(LocalDateTime.of(2012, Month.JANUARY, 1, 10, 10));
        visitModel.setVoidReason("reason");

        return visitModel;
    }

    private ConceptContext getConceptContext() {
        return ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();
    }
}
