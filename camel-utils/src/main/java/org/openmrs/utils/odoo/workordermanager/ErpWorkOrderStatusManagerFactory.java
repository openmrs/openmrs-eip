package org.openmrs.utils.odoo.workordermanager;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.rule.ErpWorkOrderStatusTransitionRule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ErpWorkOrderStatusManagerFactory {

    private List<ErpWorkOrderStatusTransitionRule> rules;

    public ErpWorkOrderStatusManagerFactory(final List<ErpWorkOrderStatusTransitionRule> rules) {
        this.rules = rules;
    }

    /**
     * returns an instance of {@link ErpWorkOrderStatusManager}
     * @param action to apply to the {@link ErpWorkOrder}
     * @param sequenceNumberIndex sequence number of the {@link ErpWorkOrder}
     *                       to which apply the above action
     * @return the instance of {@link ErpWorkOrderStatusManager}
     */
    public ErpWorkOrderStatusManager createManager(final ErpWorkOrderActionEnum action,
                                                   final Integer sequenceNumberIndex) {
        return new ErpWorkOrderStatusManager(action, sequenceNumberIndex, rules);
    }
}
