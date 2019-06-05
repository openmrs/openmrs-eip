package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.model.VisitModel;

public enum EntityNameEnum {
    PERSON(PersonModel.class),
    PATIENT(PatientModel.class),
    VISIT(VisitModel.class);

    private Class<? extends BaseModel> modelClass;

    EntityNameEnum(final Class<? extends BaseModel> modelClass) {
        this.modelClass = modelClass;
    }

    public Class<? extends BaseModel> getModelClass() {
        return modelClass;
    }

    public static EntityNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
