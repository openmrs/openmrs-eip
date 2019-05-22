package org.cicr.sync.remote.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

@Component
public class SelectPersonRoute extends RouteBuilder {

    public SelectPersonRoute(final CamelContext context) {
        super(context);
    }

    @Override
    public void configure() throws Exception {
        from("openmrsSync:person?delay=60000")
                .marshal().json(JsonLibrary.Jackson)
                .to("{{output.queue}}");
    }
}
