package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MockedMetadataEntity extends MetaDataEntity {

    public MockedMetadataEntity(final Long id,
                        final String uuid) {
        this.setId(id);
        this.setUuid(uuid);
    }
}
