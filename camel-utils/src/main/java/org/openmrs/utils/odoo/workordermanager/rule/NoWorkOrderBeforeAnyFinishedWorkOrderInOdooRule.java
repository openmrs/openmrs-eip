package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This rule ensures that no finished work order after the original work order, the reason for this rule is because
 * we can't do anything to a work order that is already finished in odoo therefore, we can't process the original work order.
 * Another camel route needs to handle it or might require manual intervention.
 */
@Component
public class NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule extends FailureRule {

    private static final Logger log = LoggerFactory.getLogger(NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule.class);

    /**
     * @see FailureRule#fail(WorkOrderStatusTransitionContext)
     */
    @Override
    boolean fail(WorkOrderStatusTransitionContext context) {
        List<WorkOrder> workOrders = context.getWorkOrders();
        List<WorkOrder> workOrdersAfterCurrent = workOrders
                .subList(context.getCurrentWorkOrderSequenceIndex() + 1, workOrders.size());

        boolean fail = workOrdersAfterCurrent.stream().anyMatch(wo -> wo.getState() == WorkOrderStateEnum.DONE);
        if (fail) {
            log.warn("Cannot process work order because there is a finished work order after it in Odoo");
        }

        return fail;
    }

}
