package org.openmrs.utils.odoo;

import org.junit.Test;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;

import static org.junit.Assert.assertEquals;

public class ErpWorkOrderActionEnumTest {

    @Test
    public void getResultingWorkOrderState_should_return_PROGRESS_if_value_START() {
        // Given
        ErpWorkOrderActionEnum value = ErpWorkOrderActionEnum.START;

        // When
        ErpWorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(ErpWorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_DONE_if_value_CLOSE() {
        // Given
        ErpWorkOrderActionEnum value = ErpWorkOrderActionEnum.CLOSE;

        // When
        ErpWorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(ErpWorkOrderStateEnum.DONE, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_PENDING_if_value_PAUSE() {
        // Given
        ErpWorkOrderActionEnum value = ErpWorkOrderActionEnum.PAUSE;

        // When
        ErpWorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(ErpWorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_CANCEL_if_value_CANCEL() {
        // Given
        ErpWorkOrderActionEnum value = ErpWorkOrderActionEnum.CANCEL;

        // When
        ErpWorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(ErpWorkOrderStateEnum.READY, result);
    }
}
