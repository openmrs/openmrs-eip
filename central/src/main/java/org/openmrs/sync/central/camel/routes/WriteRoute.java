package org.openmrs.sync.central.camel.routes;

import org.apache.camel.builder.RouteBuilder;

import org.openmrs.sync.core.service.security.PGPDecryptService;
import org.springframework.stereotype.Component;

@Component
public class WriteRoute extends RouteBuilder {

    private PGPDecryptService pgpDecryptService;

    public WriteRoute(final PGPDecryptService pgpDecryptService) {
        this.pgpDecryptService = pgpDecryptService;
    }

    @Override
    public void configure() {
        from("{{input.queue}}")
                .to("log:tut")
                .process(pgpDecryptService)
                .to("openmrsLoad");
    }
}
