package org.openmrs.sync.central.camel.routes;

import org.apache.camel.builder.RouteBuilder;

import org.springframework.stereotype.Component;

@Component
public class WriteRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("{{input.queue}}")
                .to("log:tut")
                .to("openmrsLoad");
    }
}
