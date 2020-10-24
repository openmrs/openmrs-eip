package org.openmrs.eip.component.camel;

import org.junit.Test;
import org.openmrs.eip.component.exception.EIPException;

import static org.junit.Assert.assertEquals;

public class SyncActionEnumTest {

    @Test
    public void getAction_should_return_action() {
        // Given
        String actionString = "extract";

        // When
        SyncActionEnum result = SyncActionEnum.getAction(actionString);

        // Then
        assertEquals(SyncActionEnum.EXTRACT, result);
    }

    @Test(expected = EIPException.class)
    public void getAction_should_throw_exception() {
        // Given
        String actionString = "wrong_action";

        // When
        SyncActionEnum result = SyncActionEnum.getAction(actionString);

        // Then

    }
}
