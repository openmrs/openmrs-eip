package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.model.PersonModel;

public enum TableNameEnum {
    PERSON(PersonModel.class),
    PATIENT(PatientModel.class);

    private Class<? extends OpenMrsModel> modelClass;

    TableNameEnum(final Class<? extends OpenMrsModel> modelClass) {
        this.modelClass = modelClass;
    }

    public Class<? extends OpenMrsModel> getModelClass() {
        return modelClass;
    }

    public static TableNameEnum getTableNameEnum(String tableName) {
        return valueOf(tableName.toUpperCase());
    }
}
