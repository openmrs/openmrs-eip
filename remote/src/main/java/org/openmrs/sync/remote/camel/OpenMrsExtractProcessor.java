package org.openmrs.sync.remote.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OpenMrsExtractProcessor implements Processor {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsExtractProcessor(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(final Exchange exchange) {
        if (exchange.getIn().getBody() instanceof TableSyncStatus) {
            TableSyncStatus status = (TableSyncStatus) exchange.getIn().getBody();

            List<? extends OpenMrsModel> models = entityServiceFacade.getModels(status.getTableName(), status.getLastSyncDate());

            exchange.getIn().setBody(models);
            exchange.getIn().setHeader("OpenMrsTableSyncName", status.getTableName().name());
            exchange.getIn().setHeader("CamelJacksonUnmarshalType", status.getTableName().getModelClass().getName());
        }
    }
}
