package org.cicr.sync.core.camel;

import org.cicr.sync.core.entity.OpenMrsEty;
import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.model.PersonModel;

public enum EntityNameEnum {
    PERSON(PersonEty.class, PersonModel.class);

    private Class<? extends OpenMrsEty> entityType;
    private Class<? extends OpenMrsModel> modelType;

    EntityNameEnum(final Class<? extends OpenMrsEty> entityType,
                   final Class<? extends OpenMrsModel> modelType) {
        this.entityType = entityType;
        this.modelType = modelType;
    }

    public Class<? extends OpenMrsEty> getEntityType() {
        return entityType;
    }

    public Class<? extends OpenMrsModel> getModelType() {
        return modelType;
    }

    public static EntityNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
