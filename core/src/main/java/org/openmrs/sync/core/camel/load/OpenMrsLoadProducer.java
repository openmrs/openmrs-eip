package org.openmrs.sync.core.camel.load;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.model.CamelModel;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.core.utils.JsonUtils;

public class OpenMrsLoadProducer extends DefaultProducer {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsLoadProducer(final Endpoint endpoint,
                               final EntityServiceFacade entityServiceFacade) {
        super(endpoint);
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(final Exchange exchange) {
        EntityNameEnum entityName = EntityNameEnum.getEntityNameEnum((String) exchange.getIn().getHeader("OpenMrsTableSyncName"));

        String json = (String) exchange.getIn().getBody();

        CamelModel camelModel = (CamelModel) JsonUtils.unmarshal(json, CamelModel.class.getName());

        BaseModel model = camelModel.getModel();

        entityServiceFacade.saveModel(entityName, model);
    }
}
