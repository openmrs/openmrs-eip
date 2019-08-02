package org.openmrs.sync.sender.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openmrs.sync.core.service.security.PGPEncryptService;
import org.springframework.stereotype.Component;

@Component
public class SelectRoute extends RouteBuilder {

    private SaveSyncStatusProcessor saveSyncStatusProcessor;

    private PGPEncryptService pgpEncryptService;

    public SelectRoute(final SaveSyncStatusProcessor saveSyncStatusProcessor,
                       final PGPEncryptService pgpEncryptService) {
        this.saveSyncStatusProcessor = saveSyncStatusProcessor;
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
                .process(saveSyncStatusProcessor);
    }
}
