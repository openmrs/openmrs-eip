package org.openmrs.sync.core.model;

import lombok.Data;

@Data
public class MockedModel extends OpenMrsModel {

    public MockedModel(final String uuid) {
        this.setUuid(uuid);
    }
}
