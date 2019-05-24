package org.openmrs.sync.core.camel;

import org.apache.camel.Processor;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

@UriEndpoint(
        firstVersion = "1.0.0",
        scheme = "openmrsSync",
        title = "openmrsSync",
        syntax = "openmrsSync:entityName",
        label = "core, database"
)
public class OpenMrsSyncEndpoint extends ScheduledPollEndpoint {

    @UriPath(name = "entityName")
    @Metadata(required = true)
    private TableNameEnum entityName;

    private EntityServiceFacade entityServiceFacade;

    protected OpenMrsSyncEndpoint(final String endpointUri,
                                  final OpenMrsSyncComponent component,
                                  final EntityServiceFacade entityServiceFacade) {
        super(endpointUri, component);
        this.entityName = component.getEntityName();
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public OpenMrsSyncProducer createProducer() throws Exception {
        return new OpenMrsSyncProducer(this, entityName, entityServiceFacade);
    }

    @Override
    public OpenMrsSyncConsumer createConsumer(final Processor processor) throws Exception {
        OpenMrsSyncConsumer consumer = new OpenMrsSyncConsumer(this, processor, entityName, entityServiceFacade);
        consumer.setScheduler(getScheduler());
        consumer.setDelay(getDelay());
        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
