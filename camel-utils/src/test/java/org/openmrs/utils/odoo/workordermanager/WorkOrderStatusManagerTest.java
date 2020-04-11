package org.openmrs.utils.odoo.workordermanager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.WorkOrderStatusTransitionRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class WorkOrderStatusManagerTest {

    private WorkOrderStatusManager manager;

    @Before
    public void init() {

        List<WorkOrderStatusTransitionRule> rules = Arrays.asList(
                new Test1WorkOrderStatusTransitionRule(),
                new Test2WorkOrderStatusTransitionRule()
        );

        manager = new WorkOrderStatusManager(
                ObsActionEnum.PAUSE,
                3,
                rules);
    }

    @Test
    public void manageStatus_should_change_workOrder1_and_return_workOrder1_and_workOrder3() {
        // Given
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setState(WorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.PENDING);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setState(WorkOrderStateEnum.PENDING);
        workOrder3.setId(3);
        List<WorkOrder> workOrders = Arrays.asList(workOrder2, workOrder3, workOrder1);

        // When
        List<WorkOrderAction> result = manager.manageStatus(workOrders);

        // Then
        assertEquals(2, result.size());
        assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
    }

    @Test
    public void manageStatus_shouldSkipAFinishedWorkOrderBeforeTheOriginalOrder() {
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setState(WorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.DONE);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setState(WorkOrderStateEnum.PENDING);
        workOrder3.setId(3);

        //Close all rule
        manager = new WorkOrderStatusManager(ObsActionEnum.START, 3, Collections.singletonList(new WorkOrderStatusTransitionRule() {
            @Override
            public boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context) {
                return true;
            }

            @Override
            public ObsActionEnum getAction(WorkOrderStatusTransitionContext context) {
                return ObsActionEnum.CLOSE;
            }
        }));

        List<WorkOrderAction> result = manager.manageStatus(Arrays.asList(workOrder2, workOrder3, workOrder1));

        assertEquals(2, result.size());
        assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
    }

    @Test
    public void manageStatus_shouldNotSkipAFinishedWorkOrderAfterTheOriginalOrder() {
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setState(WorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setState(WorkOrderStateEnum.DONE);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setState(WorkOrderStateEnum.READY);
        workOrder3.setId(3);
        workOrder3.setNextWorkOrderId(4);
        WorkOrder workOrder4 = new WorkOrder();
        workOrder4.setState(WorkOrderStateEnum.DONE);
        workOrder4.setId(4);

        //Close all rule
        manager = new WorkOrderStatusManager(ObsActionEnum.START, 3, Collections.singletonList(new WorkOrderStatusTransitionRule() {
            @Override
            public boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context) {
                return true;
            }

            @Override
            public ObsActionEnum getAction(WorkOrderStatusTransitionContext context) {
                return ObsActionEnum.CLOSE;
            }
        }));

        List<WorkOrderAction> result = manager.manageStatus(Arrays.asList(workOrder1, workOrder2, workOrder3, workOrder4));

        assertEquals(3, result.size());
        /*assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
        assertEquals(workOrder4, result.get(2).getWorkOrder());*/
    }

    private static class Test1WorkOrderStatusTransitionRule implements WorkOrderStatusTransitionRule {
        @Override
        public boolean workOrderMatchesCondition(final WorkOrderStatusTransitionContext context) {
            return context.getWorkOrder().getId() == 1;
        }

        @Override
        public ObsActionEnum getAction(final WorkOrderStatusTransitionContext context) {
            return ObsActionEnum.START;
        }
    }

    private static class Test2WorkOrderStatusTransitionRule implements WorkOrderStatusTransitionRule {
        @Override
        public boolean workOrderMatchesCondition(final WorkOrderStatusTransitionContext context) {
            return context.getWorkOrder().getId() == 1;
        }

        @Override
        public ObsActionEnum getAction(final WorkOrderStatusTransitionContext context) {
            return ObsActionEnum.CLOSE;
        }
    }
}
