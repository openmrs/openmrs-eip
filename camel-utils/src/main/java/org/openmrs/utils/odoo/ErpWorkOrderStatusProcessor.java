package org.openmrs.utils.odoo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusManager;
import org.openmrs.utils.odoo.workordermanager.ErpWorkOrderStatusManagerFactory;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("workOrderStatusProcessor")
public class ErpWorkOrderStatusProcessor implements Processor {

    private ErpWorkOrderStatusManagerFactory factory;

    public ErpWorkOrderStatusProcessor(final ErpWorkOrderStatusManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void process(final Exchange exchange) {
        List<ErpWorkOrder> workOrders = exchange.getIn().getBody(List.class);

        ErpWorkOrderStatusManager manager = factory.createManager(
                ErpWorkOrderActionEnum.valueOf(exchange.getProperty("workorder-state-value", String.class)),
                exchange.getProperty("workorder-sequence-nb", Integer.class));

        exchange.getIn().setBody(manager.manageStatus(workOrders));
    }
}
