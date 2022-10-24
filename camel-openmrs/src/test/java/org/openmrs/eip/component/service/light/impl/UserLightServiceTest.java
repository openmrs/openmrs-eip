package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class UserLightServiceTest {
	
	@Mock
	private OpenmrsRepository<UserLight> repository;
	
	private UserLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new UserLightService(repository);
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
		UserLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedUser(), result);
	}
	
	private UserLight getExpectedUser() {
		UserLight user = new UserLight();
		user.setCreator(USER_ID);
		user.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		user.setSystemId("admin");
		user.setPersonId(1L);
		return user;
	}
}
