package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;

public class MockedSimpleService extends AbstractSimpleService<MockedEntity> {

    public MockedSimpleService(final OpenMrsRepository<MockedEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedEntity getFakeEntity(String uuid) {
        return new MockedEntity(null, uuid);
    }
}
