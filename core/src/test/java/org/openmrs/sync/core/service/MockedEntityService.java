package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.mapper.EntityToModelMapper;
import org.openmrs.sync.core.mapper.ModelToEntityMapper;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;

public class MockedEntityService extends AbstractEntityService<MockedEntity, MockedModel> {

    public MockedEntityService(final SyncEntityRepository<MockedEntity> repository,
                               final EntityToModelMapper<MockedEntity, MockedModel> entityToModelMapper,
                               final ModelToEntityMapper<MockedModel, MockedEntity> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return null;
    }
}
