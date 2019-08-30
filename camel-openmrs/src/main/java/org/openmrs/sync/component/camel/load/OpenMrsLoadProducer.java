package org.openmrs.sync.component.camel.load;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.component.model.SyncModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.openmrs.sync.component.utils.JsonUtils;

public class OpenMrsLoadProducer extends DefaultProducer {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsLoadProducer(final Endpoint endpoint,
                               final EntityServiceFacade entityServiceFacade) {
        super(endpoint);
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(final Exchange exchange) {
        String json = (String) exchange.getIn().getBody();

        SyncModel to = JsonUtils.unmarshal(json, SyncModel.class);

        TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(to.getTableToSyncModelClass());

        entityServiceFacade.saveModel(tableToSyncEnum, to.getModel());
    }
}
