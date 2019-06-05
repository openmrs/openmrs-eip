package org.openmrs.sync.central.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

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
