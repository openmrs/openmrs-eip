package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;

public class MockedLightService extends AbstractLightService<MockedEntity> {

    public MockedLightService(final OpenMrsRepository<MockedEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedEntity getFakeEntity(String uuid) {
        return new MockedEntity(null, uuid);
    }
}
