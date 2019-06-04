package org.openmrs.sync.core.camel.load;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenMrsLoadComponent extends DefaultComponent {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsLoadComponent(final CamelContext context,
                                final EntityServiceFacade entityServiceFacade) {
        super(context);
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    protected Endpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) {
        return new OpenMrsLoadEndpoint(uri, this, entityServiceFacade);
    }
}
