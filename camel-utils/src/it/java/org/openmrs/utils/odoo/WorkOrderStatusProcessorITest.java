package org.openmrs.utils.odoo;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.utils.odoo.manager.WorkOrderStatusManager;
import org.openmrs.utils.odoo.manager.WorkOrderStatusManagerFactory;
import org.openmrs.utils.odoo.manager.rule.NoWorkOrderDoneAfterWorkOrderInProgressOrReadyRule;
import org.openmrs.utils.odoo.manager.rule.NoWorkOrderReadyBeforeWorkOrderInProgressOrDoneRule;
import org.openmrs.utils.odoo.manager.rule.OnlyOneWorkOrderInProgressRule;
import org.openmrs.utils.odoo.manager.rule.WorkOrderStatusTransitionRule;
import org.openmrs.utils.odoo.model.WorkOrder;
import org.openmrs.utils.odoo.model.WorkOrderStateEnum;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class WorkOrderStatusProcessorITest {

    @Mock
    private WorkOrderStatusManagerFactory factory;

    private List<WorkOrder> workOrders;

    private WorkOrderStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        List<WorkOrderStatusTransitionRule> rules = Arrays.asList(
                new NoWorkOrderReadyBeforeWorkOrderInProgressOrDoneRule(),
                new NoWorkOrderDoneAfterWorkOrderInProgressOrReadyRule(),
                new OnlyOneWorkOrderInProgressRule()
        );

        WorkOrderStatusManager manager = new WorkOrderStatusManager(
                ObsActionEnum.START,
                3,
                rules
        );

        when(factory.createManager(ObsActionEnum.START, 3)).thenReturn(manager);

        workOrders = initWorkOrders();

        processor = new WorkOrderStatusProcessor(factory);
    }

    @Test
    public void process_should_put_modified_work_orders_in_body() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty("obs-state-value", ObsActionEnum.START.name());
        exchange.setProperty("obs-sequence-nb", 3);
        exchange.getIn().setBody(workOrders);

        // When
        processor.process(exchange);

        // Then
        assertNotNull(exchange.getIn().getBody());
    }

    private List<WorkOrder> initWorkOrders() {
        WorkOrder workOrder1 = new WorkOrder();
        workOrder1.setId(1);
        workOrder1.setState(WorkOrderStateEnum.PROGRESS);
        workOrder1.setNextWorkOrderId(2);

        WorkOrder workOrder2 = new WorkOrder();
        workOrder2.setId(2);
        workOrder2.setState(WorkOrderStateEnum.READY);
        workOrder2.setNextWorkOrderId(3);

        WorkOrder workOrder3 = new WorkOrder();
        workOrder3.setId(3);
        workOrder3.setState(WorkOrderStateEnum.READY);
        workOrder3.setNextWorkOrderId(4);

        WorkOrder workOrder4 = new WorkOrder();
        workOrder4.setId(4);
        workOrder4.setState(WorkOrderStateEnum.PROGRESS);
        workOrder4.setNextWorkOrderId(5);

        WorkOrder workOrder5 = new WorkOrder();
        workOrder5.setId(5);
        workOrder5.setState(WorkOrderStateEnum.DONE);

        return Arrays.asList(workOrder4, workOrder1, workOrder3, workOrder5, workOrder2);
    }
}
