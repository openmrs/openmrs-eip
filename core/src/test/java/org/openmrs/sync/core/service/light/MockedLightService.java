package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.impl.context.MockedContext;


public class MockedLightService extends AbstractLightService<MockedLightEntity, MockedContext> {

    public MockedLightService(final OpenMrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity getShadowEntity(final String uuid, final MockedContext context) {
        return new MockedLightEntity(null, uuid);
    }
}
