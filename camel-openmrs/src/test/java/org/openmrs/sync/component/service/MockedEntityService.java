package org.openmrs.sync.component.service;

import org.openmrs.sync.component.MockedModel;
import org.openmrs.sync.component.entity.MockedEntity;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.repository.SyncEntityRepository;

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
