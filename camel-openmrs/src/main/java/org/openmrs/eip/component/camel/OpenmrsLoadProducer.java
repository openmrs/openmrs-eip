package org.openmrs.eip.component.camel;

import org.apache.camel.Exchange;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.openmrs.eip.component.service.facade.EntityServiceFacade;
import org.springframework.context.ApplicationContext;

public class OpenmrsLoadProducer extends AbstractOpenmrsProducer {

    public static final String DELETE_PREFIX = "DELETE:";

    public static final String PROP_REBUILD_SEARCH_INDEX = "rebuild-search-index";

    public static final String PROP_RESOURCE = "resource";

    public static final String PROP_SUB_RESOURCE = "sub-resource";

    public OpenmrsLoadProducer(final OpenmrsEndpoint endpoint,
                               final ApplicationContext applicationContext,
                               final ProducerParams params) {
        super(endpoint, applicationContext, params);
    }

    @Override
    public void process(final Exchange exchange) {
        EntityServiceFacade entityServiceFacade = (EntityServiceFacade) applicationContext.getBean("entityServiceFacade");
        SyncModel syncModel = exchange.getIn().getBody(SyncModel.class);
        TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(syncModel.getTableToSyncModelClass());

        if ("d".equals(syncModel.getMetadata().getOperation())) {
            /*switch (fields[1]) {
                case "person_name":
                    exchange.setProperty(PROP_REBUILD_SEARCH_INDEX, true);
                    exchange.setProperty("resource", "person");
                    exchange.setProperty(PROP_SUB_RESOURCE, "name");
                    break;
                case "person_attribute":
                    exchange.setProperty(PROP_REBUILD_SEARCH_INDEX, true);
                    exchange.setProperty(PROP_RESOURCE, "person");
                    exchange.setProperty(PROP_SUB_RESOURCE, "attribute");
                    break;
                case "patient_identifier":
                    exchange.setProperty(PROP_REBUILD_SEARCH_INDEX, true);
                    exchange.setProperty(PROP_RESOURCE, "patient");
                    exchange.setProperty(PROP_SUB_RESOURCE, "identifier");
                    break;
            }*/

            entityServiceFacade.delete(tableToSyncEnum, syncModel.getModel().getUuid());
        } else {
            entityServiceFacade.saveModel(tableToSyncEnum, syncModel.getModel());
        }
    }

}
