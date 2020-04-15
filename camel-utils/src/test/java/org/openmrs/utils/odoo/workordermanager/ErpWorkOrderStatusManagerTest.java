package org.openmrs.utils.odoo.workordermanager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.ErpWorkOrderStatusTransitionRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ErpWorkOrderStatusManagerTest {

    private ErpWorkOrderStatusManager manager;

    @Before
    public void init() {

        List<ErpWorkOrderStatusTransitionRule> rules = Arrays.asList(
                new Test1WorkOrderStatusTransitionRule(),
                new Test2WorkOrderStatusTransitionRule()
        );

        manager = new ErpWorkOrderStatusManager(
                ErpWorkOrderActionEnum.PAUSE,
                3,
                rules);
    }

    @Test
    public void manageStatus_should_change_workOrder1_and_return_workOrder1_and_workOrder3() {
        // Given
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        workOrder1.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder3.setId(3);
        List<ErpWorkOrder> workOrders = Arrays.asList(workOrder2, workOrder3, workOrder1);

        // When
        List<ErpWorkOrderAction> result = manager.manageStatus(workOrders);

        // Then
        assertEquals(2, result.size());
        assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
    }

    @Test
    public void manageStatus_shouldSkipAFinishedWorkOrderBeforeTheOriginalOrder() {
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        workOrder1.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.DONE);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder3.setId(3);

        //Close all rule
        manager = new ErpWorkOrderStatusManager(ErpWorkOrderActionEnum.START, 3, Collections.singletonList(new ErpWorkOrderStatusTransitionRule() {
            @Override
            public boolean workOrderMatchesCondition(ErpWorkOrderStatusTransitionContext context) {
                return true;
            }

            @Override
            public ErpWorkOrderActionEnum getAction(ErpWorkOrderStatusTransitionContext context) {
                return ErpWorkOrderActionEnum.CLOSE;
            }
        }));

        List<ErpWorkOrderAction> result = manager.manageStatus(Arrays.asList(workOrder2, workOrder3, workOrder1));

        assertEquals(2, result.size());
        assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
    }

    @Test
    public void manageStatus_shouldNotSkipAFinishedWorkOrderAfterTheOriginalOrder() {
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        workOrder1.setState(ErpWorkOrderStateEnum.PENDING);
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);
        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setState(ErpWorkOrderStateEnum.DONE);
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);
        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setState(ErpWorkOrderStateEnum.READY);
        workOrder3.setId(3);
        workOrder3.setNextWorkOrderId(4);
        ErpWorkOrder workOrder4 = new ErpWorkOrder();
        workOrder4.setState(ErpWorkOrderStateEnum.DONE);
        workOrder4.setId(4);

        //Close all rule
        manager = new ErpWorkOrderStatusManager(ErpWorkOrderActionEnum.START, 3, Collections.singletonList(new ErpWorkOrderStatusTransitionRule() {
            @Override
            public boolean workOrderMatchesCondition(ErpWorkOrderStatusTransitionContext context) {
                return true;
            }

            @Override
            public ErpWorkOrderActionEnum getAction(ErpWorkOrderStatusTransitionContext context) {
                return ErpWorkOrderActionEnum.CLOSE;
            }
        }));

        List<ErpWorkOrderAction> result = manager.manageStatus(Arrays.asList(workOrder1, workOrder2, workOrder3, workOrder4));

        assertEquals(3, result.size());
        /*assertEquals(workOrder3, result.get(0).getWorkOrder());
        assertEquals(workOrder1, result.get(1).getWorkOrder());
        assertEquals(workOrder4, result.get(2).getWorkOrder());*/
    }

    private static class Test1WorkOrderStatusTransitionRule implements ErpWorkOrderStatusTransitionRule {
        @Override
        public boolean workOrderMatchesCondition(final ErpWorkOrderStatusTransitionContext context) {
            return context.getWorkOrder().getId() == 1;
        }

        @Override
        public ErpWorkOrderActionEnum getAction(final ErpWorkOrderStatusTransitionContext context) {
            return ErpWorkOrderActionEnum.START;
        }
    }

    private static class Test2WorkOrderStatusTransitionRule implements ErpWorkOrderStatusTransitionRule {
        @Override
        public boolean workOrderMatchesCondition(final ErpWorkOrderStatusTransitionContext context) {
            return context.getWorkOrder().getId() == 1;
        }

        @Override
        public ErpWorkOrderActionEnum getAction(final ErpWorkOrderStatusTransitionContext context) {
            return ErpWorkOrderActionEnum.CLOSE;
        }
    }
}
