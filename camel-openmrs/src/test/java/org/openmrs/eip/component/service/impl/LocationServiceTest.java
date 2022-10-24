package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.LocationModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.Location;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class LocationServiceTest {
	
	@Mock
	private SyncEntityRepository<Location> repository;
	
	@Mock
	private EntityToModelMapper<Location, LocationModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<LocationModel, Location> modelToEntityMapper;
	
	private LocationService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new LocationService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.LOCATION, service.getTableToSync());
	}
}
