package org.openmrs.utils.odoo.manager.rule;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.manager.WorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

@Component
public class OnlyOneWorkOrderInProgressRule implements WorkOrderStatusTransitionRule {

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

    @Override
    public ObsActionEnum getAction(final WorkOrderStatusTransitionContext context) {
        return context.getCurrentWorkOrderSequenceNumber() < context.getOriginalWorkOrderSequenceNumber() ?
                ObsActionEnum.CLOSE :
                ObsActionEnum.CANCEL;
    }
}
