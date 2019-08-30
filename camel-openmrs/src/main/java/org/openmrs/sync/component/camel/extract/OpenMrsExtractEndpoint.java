package org.openmrs.sync.component.camel.extract;

import org.apache.camel.*;
import org.apache.camel.impl.DefaultEndpoint;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.openmrs.sync.component.camel.extract.fetchmodels.ComponentParams;
import org.openmrs.sync.component.camel.extract.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.sync.component.service.TableToSyncEnum;

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

    @UriParam(label = "consumer, advanced")
    private String uuid;

    @UriParam(label = "consumer, advanced")
    private Long entityId;

    private FetchModelsRuleEngine ruleEngine;

    public OpenMrsExtractEndpoint(final String endpointUri,
                                  final Component component,
                                  final FetchModelsRuleEngine ruleEngine,
                                  final TableToSyncEnum tableToSync) {
        super(endpointUri, component);
        this.ruleEngine = ruleEngine;
        this.tableToSync = tableToSync;
    }

    @Override
    public Producer createProducer() {
        ComponentParams params = ComponentParams.builder()
                .lastSyncDate(lastSyncDate)
                .id(entityId)
                .uuid(uuid)
                .build();
        return new OpenMrsExtractProducer(this, ruleEngine, tableToSync, params);
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public void setEntityId(final Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityId() {
        return entityId;
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
