package org.openmrs.sync.component.service.light;

import org.openmrs.sync.component.entity.light.MockedAttributeTypeLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;

public class MockedAttributeTypeLightService extends AbstractAttributeTypeLightService<MockedAttributeTypeLight> {

    public MockedAttributeTypeLightService(final OpenmrsRepository<MockedAttributeTypeLight> repository) {
        super(repository);
    }

    @Override
    protected MockedAttributeTypeLight createEntity() {
        return new MockedAttributeTypeLight(null, null);
    }
}
