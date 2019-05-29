package org.openmrs.sync.core.service;

public enum TableNameEnum {
    PERSON,
    VISIT;

    public static TableNameEnum getTableNameEnum(String tableName) {
        return valueOf(tableName.toUpperCase());
    }
}
