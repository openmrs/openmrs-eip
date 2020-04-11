package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusTransitionContext;

public interface WorkOrderStatusTransitionRule {

    /**
     * The condition that defines on which {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder} to apply the action defined in getAction
     * @param context the Camel context
     * @return boolean
     */
    boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context);

    /**
     * The action
     * @param context the Camel context
     * @return WorkOrderActionEnum
     */
    WorkOrderActionEnum getAction(WorkOrderStatusTransitionContext context);
}
