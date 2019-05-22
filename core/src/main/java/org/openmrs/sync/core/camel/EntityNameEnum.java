package org.openmrs.sync.core.camel;

public enum EntityNameEnum {
    PERSON,
    PATIENT;

    public static EntityNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
