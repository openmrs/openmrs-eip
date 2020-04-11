package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.exception.OdooException;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;

/**
 * Base class for rules that MUST result in an exception getting thrown when a work order matches the rule,
 * these are rules that when violated we can't auto resolve and should be handled by another camel route or
 * require manual resolution.
 */
public abstract class FailureRule implements WorkOrderStatusTransitionRule {

    /**
     * @see WorkOrderStatusTransitionRule#workOrderMatchesCondition(WorkOrderStatusTransitionContext)
     */
    @Override
    public boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context) {
        if (fail(context)) {
            throw new OdooException("Can't process work order with sequence number: " + context.getOriginalWorkOrderSequenceIndex());
        }

        return false;
    }

    /**
     * @see WorkOrderStatusTransitionRule#getAction(WorkOrderStatusTransitionContext)
     */
    @Override
    public WorkOrderActionEnum getAction(WorkOrderStatusTransitionContext context) {
        return null;
    }

    /**
     * Subclasses can implement this method and decide if a work order action can be processed or not
     *
     * @param context the {@link WorkOrderStatusTransitionContext} instance
     * @return a true if an exception should be thrown otherwise false
     */
    abstract boolean fail(WorkOrderStatusTransitionContext context);

}
