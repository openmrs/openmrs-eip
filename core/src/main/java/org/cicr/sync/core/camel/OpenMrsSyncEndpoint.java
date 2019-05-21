package org.cicr.sync.core.camel;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.ScheduledPollConsumer;
import org.apache.camel.support.ScheduledPollEndpoint;
import org.cicr.sync.core.service.facade.LoadEntityServiceFacade;

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
    private EntityNameEnum entityName = null;

    private LoadEntityServiceFacade loadEntityServiceFacade;

    protected OpenMrsSyncEndpoint(final String endpointUri,
                                  final OpenMrsSyncComponent component,
                                  final LoadEntityServiceFacade loadEntityServiceFacade) {
        super(endpointUri, component);
        this.entityName = component.getEntityName();
        this.loadEntityServiceFacade = loadEntityServiceFacade;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new OpenMrsSyncProducer(this, entityName);
    }

    @Override
    public Consumer createConsumer(final Processor processor) throws Exception {
        ScheduledPollConsumer consumer = new OpenMrsSyncConsumer(this, processor, entityName, loadEntityServiceFacade);
        consumer.setScheduler(getScheduler());
        consumer.setDelay(getDelay());
        return consumer;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
