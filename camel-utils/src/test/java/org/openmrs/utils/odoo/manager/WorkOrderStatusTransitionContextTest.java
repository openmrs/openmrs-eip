package org.openmrs.utils.odoo.manager;

import org.junit.Test;
import org.openmrs.utils.odoo.model.WorkOrder;

import java.util.Arrays;

import static org.junit.Assert.*;

public class WorkOrderStatusTransitionContextTest {

    @Test
    public void getWorkOrder_should_return_wo_at_the_currentworkOrderSequanceNumberIndex() {
        // Given
        WorkOrder workOrder1 = new WorkOrder();
        WorkOrder workOrder2 = new WorkOrder();
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Arrays.asList(workOrder1, workOrder2),
                0,
                1
        );

        // When
        WorkOrder result = context.getWorkOrder();

        // Then
        assertEquals(workOrder1, result);
    }
}
