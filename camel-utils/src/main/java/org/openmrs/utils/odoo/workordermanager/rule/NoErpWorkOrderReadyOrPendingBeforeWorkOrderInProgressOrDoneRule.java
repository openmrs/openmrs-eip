package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoErpWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule implements ErpWorkOrderStatusTransitionRule {

    /**
     * Tests that a {@link ErpWorkOrder} has the state set as READY when work orders are either
     * set as PROGRESS or DONE after it
     * @param context the Camel context
     * @return boolean
     */
    @Override
    public boolean workOrderMatchesCondition(final ErpWorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == ErpWorkOrderStateEnum.READY || context.getWorkOrder().getState() == ErpWorkOrderStateEnum.PENDING) {
            List<ErpWorkOrder> workOrders = context.getWorkOrders();
            List<ErpWorkOrder> workOrdersAfterCurrent = workOrders
                    .subList(context.getCurrentWorkOrderSequenceIndex() + 1, workOrders.size());
            return workOrdersAfterCurrent.stream()
                    .anyMatch(wo -> wo.getState() == ErpWorkOrderStateEnum.DONE || wo.getState() == ErpWorkOrderStateEnum.PROGRESS);
        }
        return false;
    }

    /**
     * Any {@link ErpWorkOrder} that matches the above condition is closed
     * @param context the Camel context
     * @return action
     */
    @Override
    public ErpWorkOrderActionEnum getAction(final ErpWorkOrderStatusTransitionContext context) {
        return ErpWorkOrderActionEnum.CLOSE;
    }
}
