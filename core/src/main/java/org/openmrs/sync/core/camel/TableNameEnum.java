package org.openmrs.sync.core.camel;

public enum TableNameEnum {
    PERSON,
    PATIENT;

    public static TableNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
