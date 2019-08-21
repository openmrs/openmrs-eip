package org.openmrs.sync.odoo.camel;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.UriEndpoint;
import org.openmrs.sync.odoo.service.OdooService;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "odoo",
        title = "odoo",
        syntax = "odoo",
        producerOnly = true,
        label = "core,java"
)
public class OpenMrsOdooEndpoint extends DefaultEndpoint {

    private OdooService odooService;

    public OpenMrsOdooEndpoint(final String endpointUri,
                               final Component component,
                               final OdooService odooService) {
        super(endpointUri, component);
        this.odooService = odooService;
    }

    @Override
    public Producer createProducer() {
        return new OpenMrsOdooProducer(this, odooService);
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
        if (object instanceof OpenMrsOdooEndpoint) {
            return super.equals(object);
        }
        return false;
    }
}
