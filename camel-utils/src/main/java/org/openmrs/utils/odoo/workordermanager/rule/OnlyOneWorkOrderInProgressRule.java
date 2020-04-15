package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

@Component
public class OnlyOneWorkOrderInProgressRule implements ErpWorkOrderStatusTransitionRule {

    /**
     * Tests that only one {@link ErpWorkOrder} has the state set to PROGRESS
     * @param context the Camel context
     * @return boolean
     */
    @Override
    public boolean workOrderMatchesCondition(final ErpWorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == ErpWorkOrderStateEnum.PROGRESS) {
            long workOrdersInProgress = context.getWorkOrders().stream()
                    .filter(wo -> wo.getState() == ErpWorkOrderStateEnum.PROGRESS)
                    .count();
            return workOrdersInProgress > 1;
        }
        return false;
    }

    /**
     * Any other {@link ErpWorkOrder} that matches the above condition and
     * is before the {@link ErpWorkOrder} at the originalWorkOrderSequenceNumber is closed
     * Any other {@link ErpWorkOrder} that matches the above condition and
     * is after the {@link ErpWorkOrder} at the originalWorkOrderSequenceNumber is cancelled
     * @param context the Camel context
     * @return the state
     */
    @Override
    public ErpWorkOrderActionEnum getAction(final ErpWorkOrderStatusTransitionContext context) {
        return context.getCurrentWorkOrderSequenceIndex() < context.getOriginalWorkOrderSequenceIndex() ?
                ErpWorkOrderActionEnum.CLOSE :
                ErpWorkOrderActionEnum.CANCEL;
    }
}
