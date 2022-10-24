package org.openmrs.eip.component.service;

import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.MockedModel;
import org.openmrs.eip.component.entity.MockedEntity;

public class MockedEntityService extends AbstractEntityService<MockedEntity, MockedModel> {
	
	public MockedEntityService(final SyncEntityRepository<MockedEntity> repository,
	    final EntityToModelMapper<MockedEntity, MockedModel> entityToModelMapper,
	    final ModelToEntityMapper<MockedModel, MockedEntity> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.PERSON;
	}
}
