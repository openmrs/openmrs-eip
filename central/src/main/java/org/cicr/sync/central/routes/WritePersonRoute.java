package org.cicr.sync.central.routes;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import org.cicr.sync.central.mapper.PersonToPersonEtyMapper;
import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.model.Person;
import org.springframework.stereotype.Component;

@Component
public class WritePersonRoute extends RouteBuilder {

    private PersonToPersonEtyMapper personToPersonEtyMapper;

    public WritePersonRoute(final PersonToPersonEtyMapper personToPersonEtyMapper) {
        this.personToPersonEtyMapper = personToPersonEtyMapper;
    }

    @Override
    public void configure() throws Exception {
        from("{{input.queue}}")
                .unmarshal().json(JsonLibrary.Jackson, Person.class)
                .transform().method(personToPersonEtyMapper, "apply")
                .log("${body}")
                .to("jpa:" + PersonEty.class.getName() + "?usePersist=false&flushOnSend=true&joinTransaction=true")
                .end();
    }
}
