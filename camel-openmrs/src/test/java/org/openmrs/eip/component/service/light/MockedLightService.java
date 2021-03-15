package org.openmrs.eip.component.service.light;

import org.openmrs.eip.component.entity.MockedLightEntity;
import org.openmrs.eip.component.repository.OpenmrsRepository;


public class MockedLightService extends AbstractLightService<MockedLightEntity> {

    public MockedLightService(final OpenmrsRepository<MockedLightEntity> repository) {
        super(repository);
    }

    @Override
    protected MockedLightEntity createPlaceholderEntity(final String uuid) {
        return new MockedLightEntity(1L, null);
    }
}
