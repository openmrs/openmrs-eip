package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class EncounterTypeLightServiceTest {
	
	@Mock
	private OpenmrsRepository<EncounterTypeLight> repository;
	
	private EncounterTypeLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new EncounterTypeLightService(repository);
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
		String uuid = "uuid";
		
		// When
		EncounterTypeLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedEncounterType(), result);
	}
	
	private EncounterTypeLight getExpectedEncounterType() {
		EncounterTypeLight encounterType = new EncounterTypeLight();
		encounterType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		encounterType.setCreator(USER_ID);
		encounterType.setName("[Default] - " + "uuid");
		return encounterType;
	}
}
