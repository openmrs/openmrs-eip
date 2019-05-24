package org.openmrs.sync.core.camel;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TableNameEnumTest {

    @Test
    public void getEntityNameEnum() {
        // Given
        String nameString = "person";

        // When
        TableNameEnum result = TableNameEnum.getEntityNameEnum(nameString);

        // Then
        assertEquals(TableNameEnum.PERSON, result);
    }
}
