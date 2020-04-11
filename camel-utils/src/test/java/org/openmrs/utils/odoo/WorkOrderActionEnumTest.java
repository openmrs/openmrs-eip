package org.openmrs.utils.odoo;

import org.junit.Test;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;

import static org.junit.Assert.assertEquals;

public class WorkOrderActionEnumTest {

    @Test
    public void getResultingWorkOrderState_should_return_PROGRESS_if_value_START() {
        // Given
        WorkOrderActionEnum value = WorkOrderActionEnum.START;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_DONE_if_value_CLOSE() {
        // Given
        WorkOrderActionEnum value = WorkOrderActionEnum.CLOSE;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.DONE, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_PENDING_if_value_PAUSE() {
        // Given
        WorkOrderActionEnum value = WorkOrderActionEnum.PAUSE;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_CANCEL_if_value_CANCEL() {
        // Given
        WorkOrderActionEnum value = WorkOrderActionEnum.CANCEL;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.READY, result);
    }
}
