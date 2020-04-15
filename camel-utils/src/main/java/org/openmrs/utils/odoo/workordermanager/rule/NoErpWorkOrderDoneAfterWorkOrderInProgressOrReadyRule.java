package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoErpWorkOrderDoneAfterWorkOrderInProgressOrReadyRule implements ErpWorkOrderStatusTransitionRule {

    /**
     * Tests that a {@link ErpWorkOrder} has the state set as DONE when work orders are either
     * set as PROGRESS or READY before it
     * @param context the Camel context
     * @return boolean
     */
    @Override
    public boolean workOrderMatchesCondition(final ErpWorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == ErpWorkOrderStateEnum.DONE) {
            List<ErpWorkOrder> workOrders = context.getWorkOrders();
            List<ErpWorkOrder> workOrdersBeforeCurrent = workOrders
                    .subList(0, context.getCurrentWorkOrderSequenceIndex());
            return workOrdersBeforeCurrent.stream()
                    .anyMatch(wo -> wo.getState() == ErpWorkOrderStateEnum.READY || wo.getState() == ErpWorkOrderStateEnum.PROGRESS);
        }
        return false;
    }

    /**
     * Any {@link ErpWorkOrder} that matches the above condition is cancelled
     * @param context the Camel context
     * @return action
     */
    @Override
    public ErpWorkOrderActionEnum getAction(final ErpWorkOrderStatusTransitionContext context) {
        return ErpWorkOrderActionEnum.CANCEL;
    }
}
