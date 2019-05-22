package org.openmrs.sync.core.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

public class OpenMrsSyncProducer extends DefaultProducer {

    private EntityNameEnum entityName;
    private EntityServiceFacade entityServiceFacade;

    public OpenMrsSyncProducer(final Endpoint endpoint,
                               final EntityNameEnum entityName,
                               final EntityServiceFacade entityServiceFacade) {
        super(endpoint);
        this.entityName = entityName;
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        entityServiceFacade.saveModel(entityName, (OpenMrsModel) exchange.getIn().getBody());
    }
}
