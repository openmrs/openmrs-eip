package org.openmrs.utils.odoo.workordermanager;

import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;

import java.util.List;

/**
 * The context of the execution to keep work orders in a consistent state
 * workOrders is the list of work orders of a given manufacturing order
 * currentWorkOrderSequenceIndex is the index of the work order which is being checked for consistency
 * originalWorkOrderSequenceIndex is the index of the work order upon which the {@link WorkOrderActionEnum}
 * was originally applied
 */
public class WorkOrderStatusTransitionContext {
    private List<WorkOrder> workOrders;
    private int currentWorkOrderSequenceIndex;
    private int originalWorkOrderSequenceIndex;

    public WorkOrderStatusTransitionContext(final List<WorkOrder> workOrders,
                                            final int currentWorkOrderSequenceIndex,
                                            final int originalWorkOrderSequenceIndex) {
        this.workOrders = workOrders;
        this.currentWorkOrderSequenceIndex = currentWorkOrderSequenceIndex;
        this.originalWorkOrderSequenceIndex = originalWorkOrderSequenceIndex;
    }

    public WorkOrder getWorkOrder() {
        return this.workOrders.get(currentWorkOrderSequenceIndex);
    }

    public int getOriginalWorkOrderSequenceIndex() {
        return originalWorkOrderSequenceIndex;
    }

    public List<WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public int getCurrentWorkOrderSequenceIndex() {
        return currentWorkOrderSequenceIndex;
    }
}
