package org.openmrs.utils.odoo.manager;

import org.openmrs.utils.odoo.exception.OdooException;
import org.openmrs.utils.odoo.model.WorkOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WorkOrderSorter {

    private static final Supplier<OdooException> NO_LAST_WO_EXCEPTION_SUPPLIER = () ->
            new OdooException("work orders should contain at least one work order with nextWokOrderId null");

    private List<WorkOrder> workOrders;

    public WorkOrderSorter(final List<WorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    /**
     * Sorts a list of work orders according each {@link WorkOrder} 'nextWorkOrderId'
     * Each {@link WorkOrder} has a nextWorkOrderAttribute that links to another (and only one) {@link WorkOrder} in the list
     * so that the last {@link WorkOrder} nextWorkOrderAttribute value is null
     * @return the sorted list
     */
    public List<WorkOrder> sort() {
        if (workOrders == null) {
            return null;
        }

        WorkOrder lastWorkOrder = workOrders.stream()
                .filter(wo -> wo.getNextWorkOrderId() == null)
                .findFirst().orElseThrow(NO_LAST_WO_EXCEPTION_SUPPLIER);

        return sortRecursively(lastWorkOrder);
    }

    private List<WorkOrder> sortRecursively(final WorkOrder workOrder) {

        List<WorkOrder> orderedWorkOrders = new ArrayList<>();

        workOrders.stream()
                .filter(wo -> wo.getNextWorkOrderId() != null && wo.getNextWorkOrderId() == workOrder.getId())
                .findFirst().ifPresent(previousWorkOrder -> orderedWorkOrders.addAll(sortRecursively(previousWorkOrder)));

        orderedWorkOrders.add(workOrder);

        return orderedWorkOrders;
    }
}
