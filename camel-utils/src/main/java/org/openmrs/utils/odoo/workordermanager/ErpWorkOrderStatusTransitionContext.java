package org.openmrs.utils.odoo.workordermanager;

import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

import java.util.List;

/**
 * The context of the execution to keep work orders in a consistent state
 * workOrders is the list of work orders of a given manufacturing order
 * currentWorkOrderSequenceIndex is the index of the work order which is being checked for consistency
 * originalWorkOrderSequenceIndex is the index of the work order upon which the {@link ErpWorkOrderActionEnum}
 * was originally applied
 */
public class ErpWorkOrderStatusTransitionContext {
    private List<ErpWorkOrder> workOrders;
    private int currentWorkOrderSequenceIndex;
    private int originalWorkOrderSequenceIndex;

    public ErpWorkOrderStatusTransitionContext(final List<ErpWorkOrder> workOrders,
                                               final int currentWorkOrderSequenceIndex,
                                               final int originalWorkOrderSequenceIndex) {
        this.workOrders = workOrders;
        this.currentWorkOrderSequenceIndex = currentWorkOrderSequenceIndex;
        this.originalWorkOrderSequenceIndex = originalWorkOrderSequenceIndex;
    }

    public ErpWorkOrder getWorkOrder() {
        return this.workOrders.get(currentWorkOrderSequenceIndex);
    }

    public int getOriginalWorkOrderSequenceIndex() {
        return originalWorkOrderSequenceIndex;
    }

    public List<ErpWorkOrder> getWorkOrders() {
        return workOrders;
    }

    public int getCurrentWorkOrderSequenceIndex() {
        return currentWorkOrderSequenceIndex;
    }
}
