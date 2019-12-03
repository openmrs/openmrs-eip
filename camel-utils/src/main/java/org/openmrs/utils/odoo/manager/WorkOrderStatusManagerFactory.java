package org.openmrs.utils.odoo.manager;

import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.manager.rule.WorkOrderStatusTransitionRule;
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
     * @param action to apply to the {@link org.openmrs.utils.odoo.model.WorkOrder}
     * @param sequenceNumberIndex sequence number of the {@link org.openmrs.utils.odoo.model.WorkOrder}
     *                       to which apply the above action
     * @return the instance of {@link WorkOrderStatusManager}
     */
    public WorkOrderStatusManager createManager(final ObsActionEnum action,
                                                final Integer sequenceNumberIndex) {
        return new WorkOrderStatusManager(action, sequenceNumberIndex, rules);
    }
}
