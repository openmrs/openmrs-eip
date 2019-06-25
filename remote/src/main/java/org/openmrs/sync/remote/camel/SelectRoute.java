package org.openmrs.sync.remote.camel;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class SelectRoute extends RouteBuilder {

    private SaveTableSyncStatusProcessor saveTableSyncStatusProcessor;

    public SelectRoute(final SaveTableSyncStatusProcessor saveTableSyncStatusProcessor) {
        this.saveTableSyncStatusProcessor = saveTableSyncStatusProcessor;
    }

    @Override
    public void configure() {
        from("seda:sync")
                .recipientList(simple("openmrsExtract:${body.getTableToSync().name()}?lastSyncDate=${body.getLastSyncDateAsString()}"))
                .split(body()).streaming()
                        .to("log:row")
                        .to("{{output.queue}}")
                .end()
                .process(saveTableSyncStatusProcessor);
    }
}
