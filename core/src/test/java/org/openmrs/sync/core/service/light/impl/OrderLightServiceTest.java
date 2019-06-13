package org.openmrs.sync.core.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.EncounterContext;
import org.openmrs.sync.core.service.light.impl.context.OrderContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class OrderLightServiceTest {

    @Mock
    private OpenMrsRepository<OrderLight> repository;

    @Mock
    private LightServiceNoContext<OrderTypeLight> orderTypeService;

    @Mock
    private LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    private LightServiceNoContext<ProviderLight> providerService;

    @Mock
    private LightService<EncounterLight, EncounterContext> encounterService;

    @Mock
    private LightServiceNoContext<PatientLight> patientService;

    @Mock
    private LightServiceNoContext<CareSettingLight> careSettingService;

    private OrderLightService service;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        service = new OrderLightService(repository,
                orderTypeService,
                conceptService,
                providerService,
                encounterService,
                patientService,
                careSettingService);
    }

    @Test
    public void getShadowEntity() {
        // Given
        OrderContext orderContext = OrderContext.builder()
                .orderTypeUuid("orderType")
                .careSettingUuid("careSetting")
                .conceptUuid("concept")
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .providerUuid("orderer")
                .encounterUuid("encounter")
                .encounterEncounterTypeUuid("encounterEncounterType")
                .encounterPatientUuid("encounterPatient")
                .patientUuid("patient")
                .build();
        EncounterContext encounterContext = EncounterContext.builder()
                .encounterTypeUuid("encounterEncounterType")
                .patientUuid("encounterPatient")
                .build();
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptDatatypeUuid("conceptDatatype")
                .conceptClassUuid("conceptClass")
                .build();

        when(orderTypeService.getOrInit("orderType")).thenReturn(getOrderType());
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(getConcept());
        when(providerService.getOrInit("orderer")).thenReturn(getOrderer());
        when(encounterService.getOrInit("encounter", encounterContext)).thenReturn(getEncounter());
        when(patientService.getOrInit("patient")).thenReturn(getPatient());
        when(careSettingService.getOrInit("careSetting")).thenReturn(getCareSetting());

        // When
        OrderLight result = service.getShadowEntity("UUID", orderContext);

        // Then
        assertEquals(getExpectedOrder(), result);
    }

    private OrderLight getExpectedOrder() {
        OrderLight order = new OrderLight();
        order.setUuid("UUID");
        order.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
        order.setCreator(1L);
        order.setOrderType(getOrderType());
        order.setConcept(getConcept());
        order.setOrderer(getOrderer());
        order.setEncounter(getEncounter());
        order.setPatient(getPatient());
        order.setCareSetting(getCareSetting());
        return order;
    }

    private CareSettingLight getCareSetting() {
        CareSettingLight careSetting = new CareSettingLight();
        careSetting.setUuid("careSetting");
        return careSetting;
    }

    private PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("patient");
        return patient;
    }

    private EncounterLight getEncounter() {
        EncounterLight encounter = new EncounterLight();
        encounter.setUuid("encounter");
        return encounter;
    }

    private ProviderLight getOrderer() {
        ProviderLight provider = new ProviderLight();
        provider.setUuid("orderer");
        return provider;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }

    private OrderTypeLight getOrderType() {
        OrderTypeLight orderType = new OrderTypeLight();
        orderType.setUuid("orderType");
        return orderType;
    }
}
