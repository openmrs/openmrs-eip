package org.openmrs.sync.core.camel.load;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.time.LocalDateTime;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrsLoad",
        title = "OpenMrsLoad",
        syntax = "openmrsLoad:entityName",
        producerOnly = true,
        label = "core,java"
)
public class OpenMrsLoadEndpoint extends DefaultEndpoint {

    /*@UriPath(name = "entityName")
    @Metadata(required = "true")
    private EntityNameEnum entityName;*/

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsLoadEndpoint(final String endpointUri,
                               final Component component,
                               final EntityServiceFacade entityServiceFacade) {
        super(endpointUri, component);
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public Producer createProducer() {
        return new OpenMrsLoadProducer(this, entityServiceFacade);
    }

    @Override
    public Consumer createConsumer(final Processor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
