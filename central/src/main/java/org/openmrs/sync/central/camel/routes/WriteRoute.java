package org.openmrs.sync.central.camel.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import org.openmrs.sync.central.camel.OpenMrsInsertProcessor;
import org.springframework.stereotype.Component;

@Component
public class WriteRoute extends RouteBuilder {

    private OpenMrsInsertProcessor processor;

    public WriteRoute(final OpenMrsInsertProcessor processor) {
        this.processor = processor;
    }

    @Override
    public void configure() throws Exception {
        from("{{input.queue}}")
                .unmarshal(getJsonFormat())
                .process(processor);
    }

    private JsonDataFormat getJsonFormat() {
        JsonDataFormat jsonDataFormat = new JsonDataFormat(JsonLibrary.Jackson);
        jsonDataFormat.setAllowUnmarshallType(true);
        return jsonDataFormat;
    }
}
