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

public class NoWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRuleTest {

    private NoWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule rule;

    @Before
    public void init() {
        rule = new NoWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule();
    }

    @Test
    public void workOrderMatchesCondition_should_return_true_if_state_READY_and_other_wo_present_DONE() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.DONE);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertTrue(result);
    }

    @Test
    public void workOrderMatchesCondition_should_return_true_if_state_PENDING_and_other_wo_present_DONE() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PENDING);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.DONE);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Arrays.asList(workOrder, workOrder2),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertTrue(result);
    }

    @Test
    public void workOrderMatchesCondition_should_return_true_if_state_READY_and_other_wo_present_PROGRESS() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
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
    public void workOrderMatchesCondition_should_return_true_if_state_PENDING_and_other_wo_present_PROGRESS() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PENDING);
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
    public void workOrderMatchesCondition_should_return_false_if_state_READY_and_no_other_wo_PROGRESS_nor_DONE() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.READY);
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
    public void workOrderMatchesCondition_should_return_false_if_state_PENDING_and_no_other_wo_PROGRESS_nor_DONE() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PENDING);
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
    public void workOrderMatchesCondition_should_return_false_if_state_not_READY() {
        // Given
        WorkOrder workOrder = new WorkOrder();
        workOrder.setState(WorkOrderStateEnum.PROGRESS);
        WorkOrderStatusTransitionContext context = new WorkOrderStatusTransitionContext(
                Collections.singletonList(workOrder),0, 0
        );

        // When
        boolean result = rule.workOrderMatchesCondition(context);

        // Then
        assertFalse(result);
    }

    @Test
    public void getNewState_should_return_DONE() {
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
