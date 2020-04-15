package org.openmrs.utils.odoo.workordermanager.rule;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;

import java.util.Arrays;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRuleTest {

    private NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule rule;

    @Before
    public void setup() {
        rule = new NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule();
    }

    @Test
    public void fail_shouldReturnTrueIfTheOriginalWorkOrderComesBeforeAnyFinishedWorkOrder() {
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.READY);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setState(ErpWorkOrderStateEnum.DONE);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2, workOrder3), 1, 2
        );

        assertTrue(rule.fail(context));
    }

    @Test
    public void fail_shouldReturnFalseIfNoFinishedWorkOrderComesAfterTheOriginalWorkOrder() {
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.READY);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setState(ErpWorkOrderStateEnum.PENDING);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2, workOrder3), 1, 2
        );

        assertFalse(rule.fail(context));
    }

}
