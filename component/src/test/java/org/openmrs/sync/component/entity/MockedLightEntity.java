package org.openmrs.sync.component.entity;

import org.openmrs.sync.component.entity.light.VoidableLightEntity;


public class MockedLightEntity extends VoidableLightEntity {

    public MockedLightEntity(final Long id,
                             final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
