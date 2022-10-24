package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.AttributeModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.LocationAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class LocationAttributeServiceTest {
	
	@Mock
	private SyncEntityRepository<LocationAttribute> repository;
	
	@Mock
	private EntityToModelMapper<LocationAttribute, AttributeModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<AttributeModel, LocationAttribute> modelToEntityMapper;
	
	private LocationAttributeService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new LocationAttributeService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.LOCATION_ATTRIBUTE, service.getTableToSync());
	}
}
