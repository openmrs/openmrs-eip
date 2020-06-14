package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.VoidableLightEntity;


public class MockedLightEntity extends VoidableLightEntity {

    public MockedLightEntity(final Long id,
                             final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
