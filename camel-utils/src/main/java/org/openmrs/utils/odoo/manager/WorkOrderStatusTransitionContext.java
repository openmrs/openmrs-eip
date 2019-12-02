package org.openmrs.utils.odoo.manager;

import org.openmrs.utils.odoo.model.WorkOrder;

import java.util.List;

public class WorkOrderStatusTransitionContext {
    private List<WorkOrder> workOrders;
    private int currentWorkOrderSequenceNumber;
    private int originalWorkOrderSequenceNumber;

    public WorkOrderStatusTransitionContext(final List<WorkOrder> workOrders,
                                            final int currentWorkOrderSequenceNumber,
                                            final int originalWorkOrderSequenceNumber) {
        this.workOrders = workOrders;
        this.currentWorkOrderSequenceNumber = currentWorkOrderSequenceNumber;
        this.originalWorkOrderSequenceNumber = originalWorkOrderSequenceNumber;
    }

    public WorkOrder getWorkOrder() {
        return this.workOrders.get(currentWorkOrderSequenceNumber);
    }

    public int getOriginalWorkOrderSequenceNumber() {
        return originalWorkOrderSequenceNumber;
    }

    public List<WorkOrder> getWorkOrders() {
        return workOrders;
    }

    public int getCurrentWorkOrderSequenceNumber() {
        return currentWorkOrderSequenceNumber;
    }
}
