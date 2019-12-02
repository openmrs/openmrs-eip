package org.openmrs.utils.odoo.manager.rule;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.model.WorkOrder;
import org.openmrs.utils.odoo.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.manager.WorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoWorkOrderDoneAfterWorkOrderInProgressOrReadyRule implements WorkOrderStatusTransitionRule {

    @Override
    public boolean workOrderMatchesCondition(final WorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == WorkOrderStateEnum.DONE) {
            List<WorkOrder> workOrders = context.getWorkOrders();
            List<WorkOrder> workOrdersBeforeCurrent = workOrders
                    .subList(0, context.getCurrentWorkOrderSequenceNumber());
            return workOrdersBeforeCurrent.stream()
                    .anyMatch(wo -> wo.getState() == WorkOrderStateEnum.READY || wo.getState() == WorkOrderStateEnum.PROGRESS);
        }
        return false;
    }

    @Override
    public ObsActionEnum getAction(final WorkOrderStatusTransitionContext context) {
        return ObsActionEnum.CANCEL;
    }
}
