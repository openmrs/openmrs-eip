package org.openmrs.utils.odoo.workordermanager.rule;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRuleTest {

    private NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule rule;

    @Before
    public void setup() {
        rule = new NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule();
    }

    @Test
    public void fail_shouldReturnTrueIfTheOriginalWorkOrderComesBeforeAnyFinishedWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setState(WorkOrderStateEnum.DONE);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2, workOrder3), 1, 2
        );

        assertTrue(rule.fail(context));
    }

    @Test
    public void fail_shouldReturnFalseIfNoFinishedWorkOrderComesAfterTheOriginalWorkOrder() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setState(WorkOrderStateEnum.PENDING);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2, workOrder3), 1, 2
        );

        assertFalse(rule.fail(context));
    }

}
