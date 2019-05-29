package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.repository.AuditableRepository;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.util.function.Function;

public class MockedEntityService extends AbstractEntityService<MockedEntity, MockedModel> {

    public MockedEntityService(final AuditableRepository<MockedEntity> repository,
                               final EntityMapper<MockedEntity, MockedModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableNameEnum getTableName() {
        return null;
    }
}
