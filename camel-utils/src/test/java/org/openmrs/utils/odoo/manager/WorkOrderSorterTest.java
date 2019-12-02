package org.openmrs.utils.odoo.manager;

import org.junit.Test;
import org.openmrs.utils.odoo.exception.OdooException;
import org.openmrs.utils.odoo.model.WorkOrder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;

public class WorkOrderSorterTest {

    private WorkOrderSorter sorter;

    @Test
    public void sort_should_sort_workorders() {
        // Given
        sorter = new WorkOrderSorter(createWorkOrders());

        // When
        List<WorkOrder> result = sorter.sort();

        // Then
        assertEquals(1, result.get(0).getId());
        assertEquals(2, result.get(1).getId());
        assertEquals(3, result.get(2).getId());
        assertEquals(4, result.get(3).getId());
        assertEquals(5, result.get(4).getId());
    }

    @Test
    public void sort_should_return_null() {
        // Given
        sorter = new WorkOrderSorter(null);

        // When
        List<WorkOrder> result = sorter.sort();

        // Then
        assertNull(result);
    }

    @Test(expected = OdooException.class)
    public void sort_should_throw_exception_if_no_work_order_with_nextWorkOrderId_null() {
        // Given
        List<WorkOrder> workOrders = createWorkOrders();
        workOrders.get(3).setNextWorkOrderId(1);
        sorter = new WorkOrderSorter(workOrders);

        // When
        sorter.sort();

        // Then
    }

    private List<WorkOrder> createWorkOrders() {
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);

        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);

        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setId(3);
        workOrder3.setNextWorkOrderId(4);

        WorkOrder workOrder4 = new WorkOrder();
        workOrder4.setId(4);
        workOrder4.setNextWorkOrderId(5);

        WorkOrder workOrder5 = new WorkOrder();
        workOrder5.setId(5);

        return Arrays.asList(workOrder4, workOrder1, workOrder3, workOrder5, workOrder2);
    }
}
