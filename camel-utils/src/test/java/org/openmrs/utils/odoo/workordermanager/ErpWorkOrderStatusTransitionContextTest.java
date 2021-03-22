package org.openmrs.utils.odoo.workordermanager;

import org.junit.Test;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

import java.util.Arrays;

import static org.junit.Assert.*;

public class ErpWorkOrderStatusTransitionContextTest {

    @Test
    public void getWorkOrder_should_return_wo_at_the_currentworkOrderSequanceNumberIndex() {
        // Given
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Arrays.asList(workOrder1, workOrder2),
                0,
                1
        );

        // When
        ErpWorkOrder result = context.getWorkOrder();

        // Then
        assertEquals(workOrder1, result);
    }
}
