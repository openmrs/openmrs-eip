package org.openmrs.utils.odoo.exception;

import org.junit.Test;
import org.openmrs.utils.odoo.exception.OdooException;

import static org.junit.Assert.assertEquals;

public class OdooExceptionTest {

    @Test
    public void constructor1_should_return_instance() {
        // Given
        Throwable cause = new RuntimeException();
        String message = "message";

        // When
        OdooException result = new OdooException(message, cause);

        // Then
        assertEquals(cause, result.getCause());
        assertEquals(message, result.getMessage());
    }

    @Test
    public void constructor2_should_return_instance() {
        // Given
        String message = "message";

        // When
        OdooException result = new OdooException(message);

        // Then
        assertEquals(message, result.getMessage());
    }
}
