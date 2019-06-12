package org.openmrs.sync.core.entity;

public class MockedEntity extends BaseEntity {

    public MockedEntity(final Long id,
                        final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
