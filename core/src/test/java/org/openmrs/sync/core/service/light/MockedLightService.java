package org.openmrs.sync.core.service.light;

import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeUuid;

import java.util.List;

public class MockedLightService extends AbstractLightService<MockedLightEntity> {

    public MockedLightService(final OpenMrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity getFakeEntity(final String uuid,
                                              final List<AttributeUuid> attributeUuids) {
        return new MockedLightEntity(null, uuid);
    }
}
