package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ConditionLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.LightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConditionLightServiceTest {
	
	@Mock
	private OpenmrsRepository<ConditionLight> repository;
	
	@Mock
	private LightService<PatientLight> patientService;
	
	private ConditionLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ConditionLightService(repository, patientService);
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
		String uuid = "uuid";
		
		// When
		ConditionLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedCondition(), result);
	}
	
	private ConditionLight getExpectedCondition() {
		ConditionLight condition = new ConditionLight();
		condition.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		condition.setCreator(USER_ID);
		condition.setPatient(getPatient());
		condition.setClinicalStatus("[Default]");
		return condition;
	}
	
	private PatientLight getPatient() {
		PatientLight patient = new PatientLight();
		patient.setUuid("PLACEHOLDER_PATIENT");
		return patient;
	}
}
