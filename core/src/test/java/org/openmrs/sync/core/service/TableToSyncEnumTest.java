package org.openmrs.sync.core.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableToSyncEnumTest {

    @Test
    public void getTableToSyncEnum() {
        // Given
        String nameString = "person";

        // When
        TableToSyncEnum result = TableToSyncEnum.getTableToSyncEnum(nameString);

        // Then
        assertEquals(TableToSyncEnum.PERSON, result);
    }
}
