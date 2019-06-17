package org.openmrs.sync.core.entity;

public class MockedEntity extends AuditableEntity {

    public MockedEntity(final Long id,
                        final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
