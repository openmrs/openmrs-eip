package org.openmrs.sync.sender.camel;

import org.apache.camel.builder.RouteBuilder;
import org.openmrs.sync.core.camel.TypeEnum;
import org.openmrs.sync.core.service.security.PGPEncryptService;
import org.springframework.stereotype.Component;

@Component
public class FolderSyncRoute extends RouteBuilder {

    private PGPEncryptService encryptService;

    public FolderSyncRoute(final PGPEncryptService encryptService) {
        this.encryptService = encryptService;
    }

    @Override
    public void configure() {
        from("file:/home/sco/Desktop/docs?noop=true&recursive=true&idempotentKey=${file:name}-${file:modified}&idempotentRepository=#fileSyncIdempotentRepository")
                .marshal().base64().convertBodyTo(String.class)
                .transform(body().prepend(TypeEnum.FILE.getOpeningTag()))
                .transform(body().append(TypeEnum.FILE.getClosingTag()))
                .process(encryptService)
                .to("{{camel.output.endpoint}}");
    }
}
