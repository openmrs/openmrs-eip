package org.openmrs.sync.remote.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SelectRoute extends RouteBuilder {

    private SaveEntitySyncStatusProcessor saveEntitySyncStatusProcessor;

    public SelectRoute(final SaveEntitySyncStatusProcessor saveEntitySyncStatusProcessor) {
        this.saveEntitySyncStatusProcessor = saveEntitySyncStatusProcessor;
    }

    @Override
    public void configure() {
        from("seda:sync")
                .recipientList(simple("openmrsExtract:${body.getEntityName().name()}?lastSyncDate=${body.getLastSyncDate()}"))
                .split(body()).streaming()
                        .to("log:row")
                        .to("{{output.queue}}")
                .end()
                .process(saveEntitySyncStatusProcessor);
    }
}
