package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.EncounterLight;
import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class EncounterLightServiceTest {
	
	@Mock
	private OpenmrsRepository<EncounterLight> repository;
	
	@Mock
	private LightService<PatientLight> patientService;
	
	@Mock
	private LightService<EncounterTypeLight> encounterTypeService;
	
	private EncounterLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new EncounterLightService(repository, patientService, encounterTypeService);
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
		when(patientService.getOrInitPlaceholderEntity()).thenReturn(getPatient());
		when(encounterTypeService.getOrInitPlaceholderEntity()).thenReturn(getEncounterType());
		String uuid = "uuid";
		
		// When
		EncounterLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedEncounter(), result);
	}
	
	private EncounterLight getExpectedEncounter() {
		EncounterLight encounter = new EncounterLight();
		encounter.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		encounter.setCreator(USER_ID);
		encounter.setEncounterType(getEncounterType());
		encounter.setEncounterDatetime(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		encounter.setPatient(getPatient());
		return encounter;
	}
	
	private PatientLight getPatient() {
		PatientLight patient = new PatientLight();
		patient.setUuid("PLACEHOLDER_PATIENT");
		return patient;
	}
	
	private EncounterTypeLight getEncounterType() {
		EncounterTypeLight encounterType = new EncounterTypeLight();
		encounterType.setUuid("PLACEHOLDER_PATIENT");
		return encounterType;
	}
}
