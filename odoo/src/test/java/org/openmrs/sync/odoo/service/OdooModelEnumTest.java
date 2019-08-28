package org.openmrs.sync.odoo.service;

import org.junit.Test;
import org.openmrs.sync.odoo.exeption.OdooException;

import static org.junit.Assert.*;

public class OdooModelEnumTest {

    @Test
    public void getOdooModelEnum_should_return_enum() {
        // Given
        String value = "CUSTOMER";

        // When
        OdooModelEnum result = OdooModelEnum.getOdooModelEnum(value);

        // Then
        assertEquals(OdooModelEnum.PARTNER, result);
    }

    @Test
    public void getOdooModelEnum_should_throw_exception() {
        // Given
        String value = "UNKNOWN";

        // When
        try {
            OdooModelEnum result = OdooModelEnum.getOdooModelEnum(value);

            fail();
        } catch (Exception e) {
            // Then
            assertTrue(e instanceof OdooException);
            assertEquals("No Odoo model exists for incoming model UNKNOWN", e.getMessage());
        }
    }
}
