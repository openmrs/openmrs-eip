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

    public WorkOrderStatusManager createManager(final ObsActionEnum state,
                                                final Integer sequenceNumber) {
        return new WorkOrderStatusManager(state, sequenceNumber, rules);
    }
}
