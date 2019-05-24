package org.openmrs.sync.core.service;

import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.util.function.Function;

public class MockedEntityService extends AbstractEntityService<MockedEntity, MockedModel> {

    public MockedEntityService(final OpenMrsRepository<MockedEntity> repository,
                               final Function<MockedEntity, MockedModel> etyToModelMapper,
                               final Function<MockedModel, MockedEntity> modelToEtyMapper) {
        super(repository, etyToModelMapper, modelToEtyMapper);
    }

    @Override
    public TableNameEnum getEntityName() {
        return null;
    }
}
