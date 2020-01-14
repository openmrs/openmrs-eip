package org.openmrs.sync.component.camel;

import org.junit.Test;
import org.openmrs.sync.component.camel.SyncActionEnum;
import org.openmrs.sync.component.exception.OpenmrsSyncException;

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

    @Test(expected = OpenmrsSyncException.class)
    public void getAction_should_throw_exception() {
        // Given
        String actionString = "wrong_action";

        // When
        SyncActionEnum result = SyncActionEnum.getAction(actionString);

        // Then

    }
}
