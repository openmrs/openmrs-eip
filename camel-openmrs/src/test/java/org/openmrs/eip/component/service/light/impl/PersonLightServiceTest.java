package org.openmrs.eip.component.service.light.impl;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.Month;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.PersonLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

public class PersonLightServiceTest {
	
	@Mock
	private OpenmrsRepository<PersonLight> repository;
	
	private PersonLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new PersonLightService(repository);
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
		PersonLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedPerson().getCreator(), result.getCreator());
		assertEquals(getExpectedPerson().getDateCreated(), result.getDateCreated());
		assertEquals(getExpectedPerson().isDead(), result.isDead());
		assertEquals(getExpectedPerson().isBirthdateEstimated(), result.isBirthdateEstimated());
		assertEquals(getExpectedPerson().isDeathdateEstimated(), result.isDeathdateEstimated());
	}
	
	private PersonLight getExpectedPerson() {
		PersonLight person = new PersonLight();
		person.setCreator(USER_ID);
		person.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		person.setDead(false);
		person.setBirthdateEstimated(false);
		person.setDeathdateEstimated(false);
		return person;
	}
}
