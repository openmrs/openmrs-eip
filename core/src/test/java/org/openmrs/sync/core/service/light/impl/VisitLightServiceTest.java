package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class VisitLightServiceTest {

    @Mock
    private OpenMrsRepository<VisitLight> repository;

    @Mock
    private LightServiceNoContext<PatientLight> patientService;

    @Mock
    private LightServiceNoContext<VisitTypeLight> visitTypeService;

    private VisitLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new VisitLightService(repository, patientService, visitTypeService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        VisitContext context = VisitContext.builder()
                .patientUuid("patient")
                .visitTypeUuid("visitType")
                .build();
        when(patientService.getOrInit("patient")).thenReturn(getPatient());
        when(visitTypeService.getOrInit("visitType")).thenReturn(getVisitType());

        // When
        VisitLight result = service.getShadowEntity("UUID", context);

        // Then
        assertEquals(getExpectedVisit(), result);
    }

    private VisitLight getExpectedVisit() {
        VisitLight visit = new VisitLight();
        visit.setUuid("UUID");
        visit.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        visit.setCreator(1L);
        visit.setDateStarted(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        visit.setPatient(getPatient());
        visit.setVisitType(getVisitType());
        return visit;
    }

    private VisitTypeLight getVisitType() {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid("visitType");
        return visitType;
    }

    private PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("patient");
        return patient;
    }
}
