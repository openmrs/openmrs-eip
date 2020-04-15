package org.openmrs.utils.odoo.workordermanager.rule;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class OnlyOneErpWorkOrderInProgressRuleTest {

    private OnlyOneWorkOrderInProgressRule rule;

    @Before
    public void init() {
        rule = new OnlyOneWorkOrderInProgressRule();
    }

    @Test
    public void workOrderMatchesCondition_should_return_true_if_state_PROGRESS_and_2_wo_in_PROGRESS() {
        // Given
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertTrue(result);
    }

    @Test
    public void workOrderMatchesCondition_should_return_false_if_state_PROGRESS_and_1_wo_in_PROGRESS() {
        // Given
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.READY);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertFalse(result);
    }

    @Test
    public void workOrderMatchesCondition_should_return_false_if_state_not_PROGRESS() {
        // Given
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.READY);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertFalse(result);
    }

    @Test
    public void getNewState_should_return_READY_if_current_after_original() {
        // Given
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),1, 0
        );

        // When
        ErpWorkOrderActionEnum result = rule.getAction(context);

        // Then
        assertEquals(ErpWorkOrderActionEnum.CANCEL, result);
    }

    @Test
    public void getNewState_should_return_DONE_if_current_before_original() {
        // Given
        ErpWorkOrder workOrder = new ErpWorkOrder();
        workOrder.setState(ErpWorkOrderStateEnum.PROGRESS);
        ErpWorkOrderStatusTransitionContext context = new ErpWorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),1, 2
        );

        // When
        ErpWorkOrderActionEnum result = rule.getAction(context);

        // Then
        assertEquals(ErpWorkOrderActionEnum.CLOSE, result);
    }
}
