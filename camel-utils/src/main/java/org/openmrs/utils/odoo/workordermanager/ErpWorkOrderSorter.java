package org.openmrs.utils.odoo.workordermanager;

import org.openmrs.utils.odoo.exception.OdooException;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ErpWorkOrderSorter {

    private static final Supplier<OdooException> NO_LAST_WO_EXCEPTION_SUPPLIER = () ->
            new OdooException("work orders should contain at least one work order with nextWokOrderId null");

    private List<ErpWorkOrder> workOrders;

    public ErpWorkOrderSorter(final List<ErpWorkOrder> workOrders) {
        this.workOrders = workOrders;
    }

    /**
     * Sorts a list of work orders according each {@link ErpWorkOrder} 'nextWorkOrderId'
     * Each {@link ErpWorkOrder} has a nextWorkOrderAttribute that links to another (and only one) {@link ErpWorkOrder} in the list
     * so that the last {@link ErpWorkOrder} nextWorkOrderAttribute value is null
     * @return the sorted list
     */
    public List<ErpWorkOrder> sort() {
        if (workOrders == null) {
            return null;
        }

        ErpWorkOrder lastWorkOrder = workOrders.stream()
                .filter(wo -> wo.getNextWorkOrderId() == null)
                .findFirst().orElseThrow(NO_LAST_WO_EXCEPTION_SUPPLIER);

        return sortRecursively(lastWorkOrder);
    }

    private List<ErpWorkOrder> sortRecursively(final ErpWorkOrder workOrder) {

        List<ErpWorkOrder> orderedWorkOrders = new ArrayList<>();

        workOrders.stream()
                .filter(wo -> wo.getNextWorkOrderId() != null && wo.getNextWorkOrderId() == workOrder.getId())
                .findFirst().ifPresent(previousWorkOrder -> orderedWorkOrders.addAll(sortRecursively(previousWorkOrder)));

        orderedWorkOrders.add(workOrder);

        return orderedWorkOrders;
    }
}
