package org.openmrs.utils.odoo.manager.rule;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.model.WorkOrder;
import org.openmrs.utils.odoo.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.manager.WorkOrderStatusTransitionContext;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NoWorkOrderReadyBeforeWorkOrderInProgressOrDoneRule implements WorkOrderStatusTransitionRule {

    @Override
    public boolean workOrderMatchesCondition(final WorkOrderStatusTransitionContext context) {
        if (context.getWorkOrder().getState() == WorkOrderStateEnum.READY) {
            List<WorkOrder> workOrders = context.getWorkOrders();
            List<WorkOrder> workOrdersAfterCurrent = workOrders
                    .subList(context.getCurrentWorkOrderSequenceNumber() + 1, workOrders.size());
            return workOrdersAfterCurrent.stream()
                    .anyMatch(wo -> wo.getState() == WorkOrderStateEnum.DONE || wo.getState() == WorkOrderStateEnum.PROGRESS);
        }
        return false;
    }

    @Override
    public ObsActionEnum getAction(final WorkOrderStatusTransitionContext context) {
        return ObsActionEnum.CLOSE;
    }
}
