package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MockedModel extends BaseModel {

    public MockedModel(final String uuid) {
        this.setUuid(uuid);
    }
}
