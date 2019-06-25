package org.openmrs.sync.core.camel.extract;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenMrsExtractComponent extends DefaultComponent {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsExtractComponent(final CamelContext context,
                                   final EntityServiceFacade entityServiceFacade) {
        super(context);
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    protected Endpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) {
        TableToSyncEnum tableToSync = TableToSyncEnum.getTableToSyncEnum(remaining);
        return new OpenMrsExtractEndpoint(uri, this, entityServiceFacade, tableToSync);
    }
}
