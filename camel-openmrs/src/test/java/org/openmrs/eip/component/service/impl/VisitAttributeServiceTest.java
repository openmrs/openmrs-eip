package org.openmrs.eip.component.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.model.VisitAttributeModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.entity.VisitAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;

import static org.junit.Assert.assertEquals;

public class VisitAttributeServiceTest {
	
	@Mock
	private SyncEntityRepository<VisitAttribute> repository;
	
	@Mock
	private EntityToModelMapper<VisitAttribute, VisitAttributeModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<VisitAttributeModel, VisitAttribute> modelToEntityMapper;
	
	private VisitAttributeService service;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new VisitAttributeService(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Test
	public void getTableToSync() {
		Assert.assertEquals(TableToSyncEnum.VISIT_ATTRIBUTE, service.getTableToSync());
	}
}
