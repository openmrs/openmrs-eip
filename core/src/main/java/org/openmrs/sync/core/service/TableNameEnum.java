package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.model.PersonModel;

public enum TableNameEnum {
    PERSON(PersonModel.class),
    PATIENT(PatientModel.class);

    private Class<? extends BaseModel> modelClass;

    TableNameEnum(final Class<? extends BaseModel> modelClass) {
        this.modelClass = modelClass;
    }

    public Class<? extends BaseModel> getModelClass() {
        return modelClass;
    }

    public static TableNameEnum getTableNameEnum(String tableName) {
        return valueOf(tableName.toUpperCase());
    }
}
