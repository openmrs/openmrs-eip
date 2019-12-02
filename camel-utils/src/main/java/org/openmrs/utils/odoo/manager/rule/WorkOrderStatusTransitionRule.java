package org.openmrs.utils.odoo.manager.rule;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.manager.WorkOrderStatusTransitionContext;

public interface WorkOrderStatusTransitionRule {

    boolean workOrderMatchesCondition(WorkOrderStatusTransitionContext context);

    ObsActionEnum getAction(WorkOrderStatusTransitionContext context);
}
