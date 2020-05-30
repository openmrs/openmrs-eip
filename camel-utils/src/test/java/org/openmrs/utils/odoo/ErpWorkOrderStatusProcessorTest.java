package org.openmrs.utils.odoo;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusManager;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusManagerFactory;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ErpWorkOrderStatusProcessorTest {

    @Mock
    private ErpWorkOrderStatusManagerFactory factory;

    @Mock
    private ErpWorkOrderStatusManager manager;

    private ErpWorkOrderStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new ErpWorkOrderStatusProcessor(factory);
    }

    @Test
    public void process_should_put_modified_work_orders_in_body() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty("workorder-state-value", ErpWorkOrderActionEnum.PAUSE.name());
        exchange.setProperty("workorder-sequence-nb", 1);
        ErpWorkOrder workOrder = new ErpWorkOrder();
        List<ErpWorkOrder> workOrders = Collections.singletonList(workOrder);
        exchange.getIn().setBody(Collections.singletonList(workOrder));
        when(factory.createManager(ErpWorkOrderActionEnum.PAUSE, 1)).thenReturn(manager);

        // When
        processor.process(exchange);

        // Then
        verify(manager).manageStatus(workOrders);
        assertNotNull(exchange.getIn().getBody());
    }
}
