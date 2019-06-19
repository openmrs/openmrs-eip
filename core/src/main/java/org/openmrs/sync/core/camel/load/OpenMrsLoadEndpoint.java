package org.openmrs.sync.core.camel.load;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrsLoad",
        title = "OpenMrsLoad",
        syntax = "openmrsLoad",
        producerOnly = true,
        label = "core,java"
)
public class OpenMrsLoadEndpoint extends DefaultEndpoint {

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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof OpenMrsLoadEndpoint) {
            return super.equals(object);
        }
        return false;
    }
}
