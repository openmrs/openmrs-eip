package org.openmrs.sync.core.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EntityNameEnumTest {

    @Test
    public void getEntityNameEnum() {
        // Given
        String nameString = "person";

        // When
        EntityNameEnum result = EntityNameEnum.getEntityNameEnum(nameString);

        // Then
        assertEquals(EntityNameEnum.PERSON, result);
    }
}
