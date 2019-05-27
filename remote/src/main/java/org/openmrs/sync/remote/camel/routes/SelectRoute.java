package org.openmrs.sync.remote.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.openmrs.sync.remote.camel.OpenMrsExtractProcessor;
import org.springframework.stereotype.Component;

@Component
public class SelectRoute extends RouteBuilder {

    private OpenMrsExtractProcessor extractProcessor;
    private SaveTableSyncStatusProcessor saveTableSyncStatusProcessor;

    public SelectRoute(final OpenMrsExtractProcessor extractProcessor,
                       final SaveTableSyncStatusProcessor saveTableSyncStatusProcessor) {
        this.extractProcessor = extractProcessor;
        this.saveTableSyncStatusProcessor = saveTableSyncStatusProcessor;
    }

    @Override
    public void configure() {
        from("seda:sync")
                .process(extractProcessor)
                .split(body(), (oldExchange, newExchange) -> newExchange).streaming()
                        .marshal().json(JsonLibrary.Jackson)
                        .to("log:row")
                        .to("{{output.queue}}")
                .end()
                .process(saveTableSyncStatusProcessor);
    }
}
