package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.CareSettingLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class CareSettingLightServiceTest {
	
	@Mock
	private OpenmrsRepository<CareSettingLight> repository;
	
	private CareSettingLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new CareSettingLightService(repository);
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
		CareSettingLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedCareSetting(), result);
	}
	
	private CareSettingLight getExpectedCareSetting() {
		CareSettingLight careSetting = new CareSettingLight();
		careSetting.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		careSetting.setCreator(USER_ID);
		careSetting.setCareSettingType("[Default]");
		careSetting.setName("[Default]");
		return careSetting;
	}
}
