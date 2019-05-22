package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MockedEntity extends OpenMrsEty {

    public MockedEntity(final Integer id,
                        final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
