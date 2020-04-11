package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

@Component
public class OnlyOneWorkOrderInProgressRule implements WorkOrderStatusTransitionRule {

    /**
     * Tests that only one {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} has the state set to PROGRESS
     * @param context the Camel context
     * @return boolean
     */
    @Override
    public boolean workOrderMatchesCondition(final WorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == WorkOrderStateEnum.PROGRESS) {
            long workOrdersInProgress = context.getWorkOrders().stream()
                    .filter(wo -> wo.getState() == WorkOrderStateEnum.PROGRESS)
                    .count();
            return workOrdersInProgress > 1;
        }
        return false;
    }

    /**
     * Any other {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} that matches the above condition and
     * is before the {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} at the originalWorkOrderSequenceNumber is closed
     * Any other {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} that matches the above condition and
     * is after the {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} at the originalWorkOrderSequenceNumber is cancelled
     * @param context the Camel context
     * @return the state
     */
    @Override
    public WorkOrderActionEnum getAction(final WorkOrderStatusTransitionContext context) {
        return context.getCurrentWorkOrderSequenceIndex() < context.getOriginalWorkOrderSequenceIndex() ?
                WorkOrderActionEnum.CLOSE :
                WorkOrderActionEnum.CANCEL;
    }
}
