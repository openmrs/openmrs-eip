package org.openmrs.sync.core.camel.extract;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.time.LocalDateTime;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrsExtract",
        title = "OpenMrsExtract",
        syntax = "openmrsExtract:tableToSync",
        producerOnly = true,
        label = "core,java"
)
public class OpenMrsExtractEndpoint extends DefaultEndpoint {

    @UriPath(name = "tableToSync")
    @Metadata(required = "true")
    private TableToSyncEnum tableToSync;

    @UriParam(label = "consumer,advanced")
    private LocalDateTime lastSyncDate;

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsExtractEndpoint(final String endpointUri,
                                  final Component component,
                                  final EntityServiceFacade entityServiceFacade,
                                  final TableToSyncEnum tableToSync) {
        super(endpointUri, component);
        this.entityServiceFacade = entityServiceFacade;
        this.tableToSync = tableToSync;
    }

    @Override
    public Producer createProducer() {
        return new OpenMrsExtractProducer(this, entityServiceFacade, tableToSync, lastSyncDate);
    }

    @Override
    public Consumer createConsumer(final Processor processor) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public LocalDateTime getLastSyncDate() {
        return lastSyncDate;
    }

    public void setLastSyncDate(final LocalDateTime lastSyncDate) {
        this.lastSyncDate = lastSyncDate;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        if (object instanceof OpenMrsExtractEndpoint) {
            return super.equals(object);
        }
        return false;
    }
}
