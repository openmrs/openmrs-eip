package org.openmrs.utils.odoo;

import org.junit.Test;
import org.openmrs.utils.odoo.model.WorkOrderStateEnum;

import static org.junit.Assert.assertEquals;

public class ObsActionEnumTest {

    @Test
    public void getResultingWorkOrderState_should_return_PROGRESS_if_value_START() {
        // Given
        ObsActionEnum value = ObsActionEnum.START;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_DONE_if_value_CLOSE() {
        // Given
        ObsActionEnum value = ObsActionEnum.CLOSE;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.DONE, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_PENDING_if_value_PAUSE() {
        // Given
        ObsActionEnum value = ObsActionEnum.PAUSE;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.PROGRESS, result);
    }

    @Test
    public void getResultingWorkOrderState_should_return_CANCEL_if_value_CANCEL() {
        // Given
        ObsActionEnum value = ObsActionEnum.CANCEL;

        // When
        WorkOrderStateEnum result = value.getResultingWorkOrderState();

        // Then
        assertEquals(WorkOrderStateEnum.READY, result);
    }
}
