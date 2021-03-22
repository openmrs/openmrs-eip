package org.openmrs.utils.odoo.workordermanager;

import org.junit.Test;
import org.openmrs.utils.odoo.exception.OdooException;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ErpWorkOrderSorterTest {

    private ErpWorkOrderSorter sorter;

    @Test
    public void sort_should_sort_workorders() {
        // Given
        sorter = new ErpWorkOrderSorter(createWorkOrders());

        // When
        List<ErpWorkOrder> result = sorter.sort();

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
        sorter = new ErpWorkOrderSorter(null);

        // When
        List<ErpWorkOrder> result = sorter.sort();

        // Then
        assertNull(result);
    }

    @Test(expected = OdooException.class)
    public void sort_should_throw_exception_if_no_work_order_with_nextWorkOrderId_null() {
        // Given
        List<ErpWorkOrder> workOrders = createWorkOrders();
        workOrders.get(3).setNextWorkOrderId(1);
        sorter = new ErpWorkOrderSorter(workOrders);

        // When
        sorter.sort();

        // Then
    }

    private List<ErpWorkOrder> createWorkOrders() {
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        workOrder1.setId(1);
        workOrder1.setNextWorkOrderId(2);

        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setId(2);
        workOrder2.setNextWorkOrderId(3);

        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setId(3);
        workOrder3.setNextWorkOrderId(4);

        ErpWorkOrder workOrder4 = new ErpWorkOrder();
        workOrder4.setId(4);
        workOrder4.setNextWorkOrderId(5);

        ErpWorkOrder workOrder5 = new ErpWorkOrder();
        workOrder5.setId(5);

        return Arrays.asList(workOrder4, workOrder1, workOrder3, workOrder5, workOrder2);
    }
}
