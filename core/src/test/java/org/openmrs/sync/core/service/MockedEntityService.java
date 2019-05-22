package org.openmrs.sync.core.service;

import org.openmrs.sync.core.camel.EntityNameEnum;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;

import java.util.function.Function;

public class MockedEntityService extends AbstractEntityService<MockedEntity, MockedModel> {

    private OpenMrsRepository<MockedEntity> repository;
    private Function<MockedEntity, MockedModel> etyToModelMapper;
    private Function<MockedModel, MockedEntity> modelToEtyMapper;

    public MockedEntityService(final OpenMrsRepository<MockedEntity> repository,
                               final Function<MockedEntity, MockedModel> etyToModelMapper,
                               final Function<MockedModel, MockedEntity> modelToEtyMapper) {
        this.repository = repository;
        this.etyToModelMapper = etyToModelMapper;
        this.modelToEtyMapper = modelToEtyMapper;
    }

    @Override
    public EntityNameEnum getEntityName() {
        return null;
    }

    @Override
    protected OpenMrsRepository<MockedEntity> getRepository() {
        return repository;
    }

    @Override
    protected Function<MockedEntity, MockedModel> getEntityToModelMapper() {
        return etyToModelMapper;
    }

    @Override
    protected Function<MockedModel, MockedEntity> getModelToEntityMapper() {
        return modelToEtyMapper;
    }
}
