package org.openmrs.sync.core.service;

public enum EntityNameEnum {
    PERSON,
    VISIT;

    public static EntityNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
