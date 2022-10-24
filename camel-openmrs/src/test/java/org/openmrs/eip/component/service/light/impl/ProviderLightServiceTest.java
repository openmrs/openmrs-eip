package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.ProviderLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class ProviderLightServiceTest {
	
	@Mock
	private OpenmrsRepository<ProviderLight> repository;
	
	private ProviderLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ProviderLightService(repository);
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
		ProviderLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedProvider(), result);
	}
	
	private ProviderLight getExpectedProvider() {
		ProviderLight provider = new ProviderLight();
		provider.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		provider.setCreator(USER_ID);
		return provider;
	}
}
