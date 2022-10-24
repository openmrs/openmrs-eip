package org.openmrs.eip.component.service.light.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.light.LocationAttributeTypeLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import static org.junit.Assert.assertNotNull;

public class LocationAttributeTypeLightServiceTest {
	
	@Mock
	private OpenmrsRepository<LocationAttributeTypeLight> repository;
	
	private LocationAttributeTypeLightService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new LocationAttributeTypeLightService(repository);
	}
	
	@Test
	public void createEntity() {
		assertNotNull(service.createEntity());
	}
}
