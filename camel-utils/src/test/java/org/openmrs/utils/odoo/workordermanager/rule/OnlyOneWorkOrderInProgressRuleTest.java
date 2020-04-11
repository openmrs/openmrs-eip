package org.openmrs.utils.odoo.workordermanager.rule;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class OnlyOneWorkOrderInProgressRuleTest {

    private OnlyOneWorkOrderInProgressRule rule;

    @Before
    public void init() {
        rule = new OnlyOneWorkOrderInProgressRule();
    }

    @Test
    public void workOrderMatchesCondition_should_return_true_if_state_PROGRESS_and_2_wo_in_PROGRESS() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
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
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.READY);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
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
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
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
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),1, 0
        );

        // When
        WorkOrderActionEnum result = rule.getAction(context);

        // Then
        assertEquals(WorkOrderActionEnum.CANCEL, result);
    }

    @Test
    public void getNewState_should_return_DONE_if_current_before_original() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),1, 2
        );

        // When
        WorkOrderActionEnum result = rule.getAction(context);

        // Then
        assertEquals(WorkOrderActionEnum.CLOSE, result);
    }
}
