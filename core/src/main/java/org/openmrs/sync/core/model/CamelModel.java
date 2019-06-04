package org.openmrs.sync.core.model;

import org.openmrs.sync.core.utils.JsonUtils;

public class CamelModel {

    private String className;

    private String modelJson;

    public CamelModel(final BaseModel model) {
        this.modelJson = JsonUtils.marshall(model);
        this.className = model.getClass().getName();
    }

    public BaseModel getModel() {
        return (BaseModel) JsonUtils.unmarshal(modelJson, className);
    }
}
