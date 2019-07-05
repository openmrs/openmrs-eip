package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;


public class MockedLightService extends AbstractLightService<MockedLightEntity> {

    public MockedLightService(final OpenMrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity createPlaceholderEntity(final String uuid) {
        return new MockedLightEntity(1L, null);
    }
}
