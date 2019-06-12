package org.openmrs.sync.core.entity;

import org.openmrs.sync.core.entity.light.LightEntity;

public class MockedLightEntity extends LightEntity {

    public MockedLightEntity(final Long id,
                        final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
