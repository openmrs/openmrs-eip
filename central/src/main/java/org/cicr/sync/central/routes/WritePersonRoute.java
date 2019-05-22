package org.cicr.sync.central.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import org.cicr.sync.core.model.PersonModel;
import org.springframework.stereotype.Component;

@Component
public class WritePersonRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{input.queue}}")
                .unmarshal().json(JsonLibrary.Jackson, PersonModel.class)
                .to("openmrsSync:person");
    }
}
