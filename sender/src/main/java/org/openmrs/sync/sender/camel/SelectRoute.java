package org.openmrs.sync.sender.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openmrs.sync.core.service.security.PGPEncryptService;
import org.springframework.stereotype.Component;

@Component
public class SelectRoute extends RouteBuilder {

    private SaveTableSyncStatusProcessor saveTableSyncStatusProcessor;

    private PGPEncryptService pgpEncryptService;

    public SelectRoute(final SaveTableSyncStatusProcessor saveTableSyncStatusProcessor,
                       final PGPEncryptService pgpEncryptService) {
        this.saveTableSyncStatusProcessor = saveTableSyncStatusProcessor;
        this.pgpEncryptService = pgpEncryptService;
    }

    @Override
    public void configure() {
        from("seda:sync")
                .recipientList(simple("openmrsExtract:${body.getTableToSync().name()}?lastSyncDate=${body.getLastSyncDateAsString()}"))
                .split(body()).streaming()
                        .process(pgpEncryptService)
                        .to("{{camel.output.endpoint}}")
                .end()
                .process(saveTableSyncStatusProcessor);

        /*from("timer://runOnce?repeatCount=1")
                .autoStartup(true)
                .recipientList(simple("openmrsExtract:person?entityId=7"))
                .split(body()).streaming()
                .to("log:row")
                .to("{{output.queue}}")
                .end();*/
    }
}
