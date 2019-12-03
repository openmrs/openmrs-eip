package org.openmrs.utils.odoo.manager.rule;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.manager.WorkOrderStatusTransitionContext;

public interface WorkOrderStatusTransitionRule {

    /**
     * The condition that defines on which {@link org.openmrs.utils.odoo.model.WorkOrder} to apply the action defined in getAction
     * @param context the Camel context
     * @return boolean
     */
    boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context);

    /**
     * The action
     * @param context the Camel context
     * @return ObsActionEnum
     */
    ObsActionEnum getAction(WorkOrderStatusTransitionContext context);
}
