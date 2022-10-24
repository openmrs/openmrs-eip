package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.ConceptModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.Concept;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class ConceptServiceTest {
	
	@Mock
	private SyncEntityRepository<Concept> repository;
	
	@Mock
	private EntityToModelMapper<Concept, ConceptModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<ConceptModel, Concept> modelToEntityMapper;
	
	private ConceptService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new ConceptService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.CONCEPT, service.getTableToSync());
	}
}
