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
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule;
import org.openmrs.utils.odoo.workordermanager.rule.NoWorkOrderDoneAfterWorkOrderInProgressOrReadyRule;
import org.openmrs.utils.odoo.workordermanager.rule.NoWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule;
import org.openmrs.utils.odoo.workordermanager.rule.OnlyOneWorkOrderInProgressRule;
import org.openmrs.utils.odoo.workordermanager.rule.WorkOrderStatusTransitionRule;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
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
                new NoWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule(),
                new NoWorkOrderDoneAfterWorkOrderInProgressOrReadyRule(),
                new OnlyOneWorkOrderInProgressRule(),
                new NoWorkOrderBeforeAnyFinishedWorkOrderInOdooRule()
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

    /**
     * Following work orders:
     * ____________________________________________
     * |    4     |     1    |   3   |   5  |   2   |
     * |__________|__________|_______|______|______ |
     * | PROGRESS | PROGRESS | READY | PROGRESS | READY |
     *
     * START Work Order 3 should return:
     * ________________________________________
     * |    3     |   1  |   2  |   4   |   5   |
     * |__________|______|______|_______|_______|
     * | PROGRESS | DONE | DONE | READY | READY |
     */
    @Test
    public void process_START_on_wo_3() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.setProperty("workorder-state-value", ObsActionEnum.START.name());
        exchange.setProperty("workorder-sequence-nb", 3);
        exchange.getIn().setBody(workOrders);

        // When
        processor.process(exchange);

        // Then
        assertNotNull(exchange.getIn().getBody());
        assertEquals(3, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(0).getWorkOrder().getId());
        assertEquals(WorkOrderStateEnum.PROGRESS, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(0).getWorkOrder().getState());
        assertEquals(1, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(1).getWorkOrder().getId());
        assertEquals(WorkOrderStateEnum.DONE, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(1).getWorkOrder().getState());
        assertEquals(2, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(2).getWorkOrder().getId());
        assertEquals(WorkOrderStateEnum.DONE, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(2).getWorkOrder().getState());
        assertEquals(4, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(3).getWorkOrder().getId());
        assertEquals(WorkOrderStateEnum.READY, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(3).getWorkOrder().getState());
        assertEquals(5, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(4).getWorkOrder().getId());
        assertEquals(WorkOrderStateEnum.READY, ((List<WorkOrderAction>) exchange.getIn().getBody()).get(4).getWorkOrder().getState());
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
        workOrder5.setState(WorkOrderStateEnum.PROGRESS);

        return Arrays.asList(workOrder4, workOrder1, workOrder3, workOrder5, workOrder2);
    }
}
