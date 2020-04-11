package org.openmrs.utils.odoo.workordermanager;

import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.rule.WorkOrderStatusTransitionRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WorkOrderStatusManagerFactory {

    private List<WorkOrderStatusTransitionRule> rules;

    public WorkOrderStatusManagerFactory(final List<WorkOrderStatusTransitionRule> rules) {
        this.rules = rules;
    }

    /**
     * returns an instance of {@link WorkOrderStatusManager}
     * @param action to apply to the {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder}
     * @param sequenceNumberIndex sequence number of the {@link org.openmrs.utils.odoo.workordermanager.model.WorkOrder}
     *                       to which apply the above action
     * @return the instance of {@link WorkOrderStatusManager}
     */
    public WorkOrderStatusManager createManager(final WorkOrderActionEnum action,
                                                final Integer sequenceNumberIndex) {
        return new WorkOrderStatusManager(action, sequenceNumberIndex, rules);
    }
}
