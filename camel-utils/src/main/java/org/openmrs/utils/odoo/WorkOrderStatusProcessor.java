package org.openmrs.utils.odoo;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.utils.odoo.manager.WorkOrderStatusManager;
import org.openmrs.utils.odoo.manager.WorkOrderStatusManagerFactory;
import org.openmrs.utils.odoo.model.WorkOrder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("workOrderStatusProcessor")
public class WorkOrderStatusProcessor implements Processor {

    private WorkOrderStatusManagerFactory factory;

    public WorkOrderStatusProcessor(final WorkOrderStatusManagerFactory factory) {
        this.factory = factory;
    }

    @Override
    public void process(final Exchange exchange) {
        List<WorkOrder> workOrders = exchange.getIn().getBody(List.class);

        WorkOrderStatusManager manager = factory.createManager(
                ObsActionEnum.valueOf(exchange.getProperty("obs-state-value", String.class)),
                exchange.getProperty("obs-sequence-nb", Integer.class));

        exchange.getIn().setBody(manager.manageStatus(workOrders));
    }
}
