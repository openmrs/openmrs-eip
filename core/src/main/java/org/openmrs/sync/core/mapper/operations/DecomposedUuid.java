package org.openmrs.sync.core.mapper.operations;

import lombok.Getter;
import org.openmrs.sync.core.entity.light.LightEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;

@Getter
public class DecomposedUuid {

    private Class<? extends LightEntity> entityType;

    private String uuid;

    public DecomposedUuid(final String entityTypeName, final String uuid) {
        this.entityType = convertToClass(entityTypeName);
        this.uuid = uuid;
    }

    private Class<? extends LightEntity> convertToClass(final String entityTypeName) {
        try {
            return (Class<? extends LightEntity>) Class.forName(entityTypeName);
        } catch (ClassNotFoundException e) {
            throw new OpenMrsSyncException("No entity class exists with the name " + entityTypeName);
        }
    }
}
