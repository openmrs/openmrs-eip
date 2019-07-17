package org.openmrs.sync.core.entity;

import org.openmrs.sync.core.entity.light.VoidableLightEntity;


public class MockedLightEntity extends VoidableLightEntity {

    public MockedLightEntity(final Long id,
                             final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
