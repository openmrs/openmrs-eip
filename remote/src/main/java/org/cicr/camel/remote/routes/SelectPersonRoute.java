package org.cicr.camel.remote.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.cicr.camel.remote.mapper.PersonEtyToPersonMapper;
import org.springframework.stereotype.Component;

@Component
public class SelectPersonRoute extends RouteBuilder {

    private SelectPersonProcessor selectPersonProcessor;
    private PersonEtyToPersonMapper personEtyToPersonMapper;

    public SelectPersonRoute(final CamelContext context,
                             final SelectPersonProcessor selectPersonProcessor,
                             final PersonEtyToPersonMapper personEtyToPersonMapper) {
        super(context);
        this.selectPersonProcessor = selectPersonProcessor;
        this.personEtyToPersonMapper = personEtyToPersonMapper;
    }

    @Override
    public void configure() throws Exception {
        //from("quartz2://myGroup/myTimerName?cron=0+0/5+12-18+?+*+MON-FRI")
        from("timer://runOnce?repeatCount=1&delay=5000")
                .process(selectPersonProcessor)
                .split(body()).streaming()
                .transform().method(personEtyToPersonMapper, "apply")
                .marshal().json(JsonLibrary.Jackson)
                .to("log:row")
                .to("{{output.queue}}")
                .end();
    }
}
