package org.openmrs.sync.core.camel;

import org.apache.camel.support.DefaultComponent;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenMrsSyncComponent extends DefaultComponent {

    private EntityServiceFacade entityServiceFacade;

    private TableNameEnum entityName;

    public OpenMrsSyncComponent(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    protected OpenMrsSyncEndpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) throws Exception {
        this.entityName = TableNameEnum.getEntityNameEnum(remaining.toUpperCase());

        OpenMrsSyncEndpoint endpoint = new OpenMrsSyncEndpoint(uri, this, entityServiceFacade);

        endpoint.setConsumerProperties(parameters);
        setProperties(endpoint, parameters);

        return endpoint;
    }

    public TableNameEnum getEntityName() {
        return entityName;
    }
}
