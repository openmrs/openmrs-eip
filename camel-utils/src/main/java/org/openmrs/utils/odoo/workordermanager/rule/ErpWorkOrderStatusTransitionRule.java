package org.openmrs.utils.odoo.workordermanager.rule;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusTransitionContext;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

public interface ErpWorkOrderStatusTransitionRule {

    /**
     * The condition that defines on which {@link ErpWorkOrder} to apply the action defined in getAction
     * @param context the Camel context
     * @return boolean
     */
    boolean workOrderMatchesCondition(ErpWorkOrderStatusTransitionContext context);

    /**
     * The action
     * @param context the Camel context
     * @return WorkOrderActionEnum
     */
    ErpWorkOrderActionEnum getAction(ErpWorkOrderStatusTransitionContext context);
}
