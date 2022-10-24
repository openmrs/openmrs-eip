package org.openmrs.eip.component.service.light.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.OrderTypeLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class OrderTypeLightServiceTest {
	
	@Mock
	private OpenmrsRepository<OrderTypeLight> repository;
	
	private OrderTypeLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new OrderTypeLightService(repository);
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
		OrderTypeLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedCareSetting(), result);
	}
	
	private OrderTypeLight getExpectedCareSetting() {
		OrderTypeLight orderType = new OrderTypeLight();
		orderType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		orderType.setCreator(USER_ID);
		orderType.setName("[Default]");
		orderType.setJavaClassName("[Default]");
		return orderType;
	}
}
