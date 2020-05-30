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
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule;
import org.openmrs.utils.odoo.workordermanager.rule.NoErpWorkOrderDoneAfterWorkOrderInProgressOrReadyRule;
import org.openmrs.utils.odoo.workordermanager.rule.NoErpWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule;
import org.openmrs.utils.odoo.workordermanager.rule.OnlyOneWorkOrderInProgressRule;
import org.openmrs.utils.odoo.workordermanager.rule.ErpWorkOrderStatusTransitionRule;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class ErpWorkOrderStatusProcessorITest {

    @Mock
    private ErpWorkOrderStatusManagerFactory factory;

    private List<ErpWorkOrder> workOrders;

    private ErpWorkOrderStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        List<ErpWorkOrderStatusTransitionRule> rules = Arrays.asList(
                new NoErpWorkOrderReadyOrPendingBeforeWorkOrderInProgressOrDoneRule(),
                new NoErpWorkOrderDoneAfterWorkOrderInProgressOrReadyRule(),
                new OnlyOneWorkOrderInProgressRule(),
                new NoErpWorkOrderBeforeAnyFinishedWorkOrderInOdooRule()
        );

        ErpWorkOrderStatusManager manager = new ErpWorkOrderStatusManager(
                ErpWorkOrderActionEnum.START,
                3,
                rules
        );

        when(factory.createManager(ErpWorkOrderActionEnum.START, 3)).thenReturn(manager);

        workOrders = initWorkOrders();

        processor = new ErpWorkOrderStatusProcessor(factory);
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
        exchange.setProperty("workorder-state-value", ErpWorkOrderActionEnum.START.name());
        exchange.setProperty("workorder-sequence-nb", 3);
        exchange.getIn().setBody(workOrders);

        // When
        processor.process(exchange);

        // Then
        assertNotNull(exchange.getIn().getBody());
        assertEquals(3, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(0).getWorkOrder().getId());
        assertEquals(ErpWorkOrderStateEnum.PROGRESS, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(0).getWorkOrder().getState());
        assertEquals(1, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(1).getWorkOrder().getId());
        assertEquals(ErpWorkOrderStateEnum.DONE, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(1).getWorkOrder().getState());
        assertEquals(2, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(2).getWorkOrder().getId());
        assertEquals(ErpWorkOrderStateEnum.DONE, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(2).getWorkOrder().getState());
        assertEquals(4, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(3).getWorkOrder().getId());
        assertEquals(ErpWorkOrderStateEnum.READY, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(3).getWorkOrder().getState());
        assertEquals(5, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(4).getWorkOrder().getId());
        assertEquals(ErpWorkOrderStateEnum.READY, ((List<ErpWorkOrderAction>) exchange.getIn().getBody()).get(4).getWorkOrder().getState());
    }

    private List<ErpWorkOrder> initWorkOrders() {
        ErpWorkOrder workOrder1 = new ErpWorkOrder();
        workOrder1.setId(1);
        workOrder1.setState(ErpWorkOrderStateEnum.PROGRESS);
        workOrder1.setNextWorkOrderId(2);

        ErpWorkOrder workOrder2 = new ErpWorkOrder();
        workOrder2.setId(2);
        workOrder2.setState(ErpWorkOrderStateEnum.READY);
        workOrder2.setNextWorkOrderId(3);

        ErpWorkOrder workOrder3 = new ErpWorkOrder();
        workOrder3.setId(3);
        workOrder3.setState(ErpWorkOrderStateEnum.READY);
        workOrder3.setNextWorkOrderId(4);

        ErpWorkOrder workOrder4 = new ErpWorkOrder();
        workOrder4.setId(4);
        workOrder4.setState(ErpWorkOrderStateEnum.PROGRESS);
        workOrder4.setNextWorkOrderId(5);

        ErpWorkOrder workOrder5 = new ErpWorkOrder();
        workOrder5.setId(5);
        workOrder5.setState(ErpWorkOrderStateEnum.PROGRESS);

        return Arrays.asList(workOrder4, workOrder1, workOrder3, workOrder5, workOrder2);
    }
}
