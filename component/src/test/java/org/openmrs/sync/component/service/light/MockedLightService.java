package org.openmrs.sync.component.service.light;

import org.openmrs.sync.component.entity.MockedLightEntity;
import org.openmrs.sync.component.repository.OpenMrsRepository;


public class MockedLightService extends AbstractLightService<MockedLightEntity> {

    public MockedLightService(final OpenMrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity createPlaceholderEntity(final String uuid) {
        return new MockedLightEntity(1L, null);
    }
}
