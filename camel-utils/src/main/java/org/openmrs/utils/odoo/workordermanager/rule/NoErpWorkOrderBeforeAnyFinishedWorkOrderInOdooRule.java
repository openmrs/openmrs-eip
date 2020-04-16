package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
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
public class NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule extends FailureRule {

    private static final Logger log = LoggerFactory.getLogger(NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule.class);

    /**
     * @see FailureRule#fail(ErpWorkOrderStatusTransitionContext)
     */
    @Override
    boolean fail(ErpWorkOrderStatusTransitionContext context) {
        List<ErpWorkOrder> workOrders = context.getWorkOrders();
        List<ErpWorkOrder> workOrdersAfterCurrent = workOrders
                .subList(context.getCurrentWorkOrderSequenceIndex() + 1, workOrders.size());

        boolean fail = workOrdersAfterCurrent.stream().anyMatch(wo -> wo.getState() == ErpWorkOrderStateEnum.DONE);
        if (fail) {
            log.error("Cannot process work order because there is a finished work order after it in Odoo");
        }

        return fail;
    }

}
