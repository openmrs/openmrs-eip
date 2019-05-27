package org.openmrs.sync.core.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableNameEnumTest {

    @Test
    public void getTableNameEnum() {
        // Given
        String nameString = "person";

        // When
        TableNameEnum result = TableNameEnum.getTableNameEnum(nameString);

        // Then
        assertEquals(TableNameEnum.PERSON, result);
    }
}
