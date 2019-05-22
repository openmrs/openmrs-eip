package org.cicr.sync.core.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.support.ScheduledPollConsumer;
import org.cicr.sync.core.model.OpenMrsModel;
import org.cicr.sync.core.service.facade.EntityServiceFacade;

import java.util.List;

public class OpenMrsSyncConsumer extends ScheduledPollConsumer {

    private EntityNameEnum entityName;

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsSyncConsumer(final Endpoint endpoint,
                               final Processor processor,
                               final EntityNameEnum entityName,
                               final EntityServiceFacade entityServiceFacade) {
        super(endpoint, processor);
        this.entityName = entityName;
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    protected int poll() throws Exception {

        List<? extends OpenMrsModel> models = entityServiceFacade.getModels(entityName);

        for (OpenMrsModel model : models) {
            Exchange exchange = getEndpoint().createExchange();
            exchange.getIn().setBody(model);
            getProcessor().process(exchange);
        }

        return models.size();
    }
}
