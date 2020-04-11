package org.openmrs.utils.odoo;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusManager;
import org.openmrs.utils.odoo.workordermanager.WorkOrderStatusManagerFactory;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class WorkOrderStatusProcessorTest {

    @Mock
    private WorkOrderStatusManagerFactory factory;

    @Mock
    private WorkOrderStatusManager manager;

    private WorkOrderStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new WorkOrderStatusProcessor(factory);
    }

    @Test
    public void process_should_put_modified_work_orders_in_body() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty("workorder-state-value", WorkOrderActionEnum.PAUSE.name());
        exchange.setProperty("workorder-sequence-nb", 1);
        WorkOrder workOrder = new WorkOrder();
        List<WorkOrder> workOrders = Collections.singletonList(workOrder);
        exchange.getIn().setBody(Collections.singletonList(workOrder));
        when(factory.createManager(WorkOrderActionEnum.PAUSE, 1)).thenReturn(manager);

        // When
        processor.process(exchange);

        // Then
        verify(manager).manageStatus(workOrders);
        assertNotNull(exchange.getIn().getBody());
    }
}
