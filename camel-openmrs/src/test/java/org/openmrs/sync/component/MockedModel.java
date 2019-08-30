package org.openmrs.sync.component;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.model.BaseModel;

@Data
@EqualsAndHashCode(callSuper = true)
public class MockedModel extends BaseModel {

    private String field1;

    private String field2;

    private String linkedEntityUuid;

    public MockedModel() {}

    public MockedModel(final String uuid) {
        this.setUuid(uuid);
    }
}
