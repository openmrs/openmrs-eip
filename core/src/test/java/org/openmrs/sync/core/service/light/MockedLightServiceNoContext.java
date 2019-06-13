package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;

public class MockedLightServiceNoContext extends AbstractLightServiceNoContext<MockedLightEntity> {

    public MockedLightServiceNoContext(final OpenMrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity getShadowEntity(final String uuid) {
        return new MockedLightEntity(null, uuid);
    }
}
