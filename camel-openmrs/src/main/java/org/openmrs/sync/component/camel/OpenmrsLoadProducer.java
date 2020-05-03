package org.openmrs.sync.component.camel;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.openmrs.sync.component.model.SyncModel;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.service.facade.EntityServiceFacade;
import org.openmrs.sync.component.utils.JsonUtils;
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

        String json = (String) exchange.getIn().getBody();

        if (json.startsWith(DELETE_PREFIX)) {
            String[] fields = StringUtils.split(json.trim(), ":");
            TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(fields[1].trim());
            switch (fields[1]) {
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
            }

            entityServiceFacade.delete(tableToSyncEnum, fields[2].trim());
        } else {
            SyncModel to = JsonUtils.unmarshal(json, SyncModel.class);

            TableToSyncEnum tableToSyncEnum = TableToSyncEnum.getTableToSyncEnum(to.getTableToSyncModelClass());

            entityServiceFacade.saveModel(tableToSyncEnum, to.getModel());
        }
    }

}
