package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.ObservationModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.Observation;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class ObservationServiceTest {
	
	@Mock
	private SyncEntityRepository<Observation> repository;
	
	@Mock
	private EntityToModelMapper<Observation, ObservationModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<ObservationModel, Observation> modelToEntityMapper;
	
	private ObservationService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ObservationService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.OBS, service.getTableToSync());
	}
}
