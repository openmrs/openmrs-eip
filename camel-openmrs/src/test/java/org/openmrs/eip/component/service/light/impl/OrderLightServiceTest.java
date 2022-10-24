package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.OrderLight;
import org.openmrs.eip.component.entity.light.OrderTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.ProviderLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.component.service.light.AbstractLightService.DEFAULT_STRING;

public class OrderLightServiceTest {
	
	@Mock
	private OpenmrsRepository<OrderLight> repository;
	
	@Mock
	private LightService<OrderTypeLight> orderTypeService;
	
	@Mock
	private LightService<ConceptLight> conceptService;
	
	@Mock
	private LightService<ProviderLight> providerService;
	
	@Mock
	private LightService<EncounterLight> encounterService;
	
	@Mock
	private LightService<PatientLight> patientService;
	
	@Mock
	private LightService<CareSettingLight> careSettingService;
	
	private OrderLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new OrderLightService(repository, orderTypeService, conceptService, providerService, encounterService,
		        patientService, careSettingService);
		UserLight user = new UserLight();
		user.setId(USER_ID);
		SyncContext.setAppUser(user);
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void createPlaceholderEntity() {
		// Given
		when(orderTypeService.getOrInitPlaceholderEntity()).thenReturn(getOrderType());
		when(conceptService.getOrInitPlaceholderEntity()).thenReturn(getConcept());
		when(providerService.getOrInitPlaceholderEntity()).thenReturn(getOrderer());
		when(encounterService.getOrInitPlaceholderEntity()).thenReturn(getEncounter());
		when(patientService.getOrInitPlaceholderEntity()).thenReturn(getPatient());
		when(careSettingService.getOrInitPlaceholderEntity()).thenReturn(getCareSetting());
		String uuid = "uuid";
		
		// When
		OrderLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedOrder(), result);
	}
	
	private OrderLight getExpectedOrder() {
		OrderLight order = new OrderLight();
		order.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		order.setCreator(USER_ID);
		order.setOrderType(getOrderType());
		order.setConcept(getConcept());
		order.setOrderer(getOrderer());
		order.setEncounter(getEncounter());
		order.setPatient(getPatient());
		order.setCareSetting(getCareSetting());
		order.setUrgency(DEFAULT_STRING);
		order.setAction(DEFAULT_STRING);
		order.setOrderNumber(DEFAULT_STRING);
		return order;
	}
	
	private CareSettingLight getCareSetting() {
		CareSettingLight careSetting = new CareSettingLight();
		careSetting.setUuid("PLACEHOLDER_CARE_SETTING");
		return careSetting;
	}
	
	private PatientLight getPatient() {
		PatientLight patient = new PatientLight();
		patient.setUuid("PLACEHOLDER_PATIENT");
		return patient;
	}
	
	private EncounterLight getEncounter() {
		EncounterLight encounter = new EncounterLight();
		encounter.setUuid("PLACEHOLDER_ENCOUNTER");
		return encounter;
	}
	
	private ProviderLight getOrderer() {
		ProviderLight provider = new ProviderLight();
		provider.setUuid("PLACEHOLDER_PROVIDER");
		return provider;
	}
	
	private ConceptLight getConcept() {
		ConceptLight concept = new ConceptLight();
		concept.setUuid("PLACEHOLDER_CONCEPT");
		return concept;
	}
	
	private OrderTypeLight getOrderType() {
		OrderTypeLight orderType = new OrderTypeLight();
		orderType.setUuid("PLACEHOLDER_ORDER_TYPE");
		return orderType;
	}
}
