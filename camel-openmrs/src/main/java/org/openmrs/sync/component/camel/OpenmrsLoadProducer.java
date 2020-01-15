package org.openmrs.sync.component.camel;

import org.apache.camel.Exchange;
import org.openmrs.sync.component.model.SyncModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.openmrs.sync.component.utils.JsonUtils;
import org.springframework.context.ApplicationContext;

public class OpenmrsLoadProducer extends AbstractOpenmrsProducer {

    public OpenmrsLoadProducer(final OpenmrsEndpoint endpoint,
                               final ApplicationContext applicationContext,
                               final ProducerParams params) {
        super(endpoint, applicationContext, params);
    }

    @Override
    public void process(final Exchange exchange) {
        EntityServiceFacade entityServiceFacade = (EntityServiceFacade) applicationContext.getBean("entityServiceFacade");

        String json = (String) exchange.getIn().getBody();

        SyncModel to = JsonUtils.unmarshal(json, SyncModel.class);

        TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(to.getTableToSyncModelClass());

        entityServiceFacade.saveModel(tableToSyncEnum, to.getModel());
    }
}
